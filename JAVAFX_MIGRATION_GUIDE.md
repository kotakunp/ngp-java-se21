# JavaFX Migration Guide

## Purpose

This document defines the recommended migration path from the current Java 8 Swing desktop application to a modern Java 21 LTS desktop application built with JavaFX, a streaming audio engine, a real build system, and native packaging.

This is not a "rewrite everything at once" plan.

The recommended approach is:

1. Keep the refactored core logic that already exists.
2. Move the build and runtime baseline to Java 21.
3. Introduce a modern project structure with Gradle.
4. Rebuild the UI layer in JavaFX.
5. Replace the long-audio playback path with a streaming audio engine.
6. Package the app with `jpackage`.

The goal is not only compatibility with a newer JDK. The real goal is a desktop app that is easier to maintain, easier to test, and capable of supporting larger files and better features.

---

## Target Stack

### Runtime

- Java 21 LTS
- JavaFX
- `javafx.concurrent`
- `javax.sound.sampled`
- `jpackage`

### Build and Packaging

- Gradle
- Gradle toolchains for Java 21
- JavaFX Gradle plugin
- `jpackage` packaging task

### UI and Rendering

- JavaFX scene graph for standard UI
- JavaFX `Canvas` for waveform/timeline rendering
- JavaFX properties and bindings for screen state

### Audio

- `Clip` only where preloaded audio is acceptable
- `SourceDataLine` for long-file streaming playback
- background waveform analysis and cache generation

### Architecture

- layered modules
- service-oriented application logic
- MVVM or a thin presentation-model pattern

---

## Why This Stack

### Why JavaFX

JavaFX is a better long-term UI foundation than Swing for this application because:

- the app is heavily interactive and state-driven
- the timeline editor is custom rendered
- the preview is dynamic
- async loading is important
- desktop packaging and modern structure matter

JavaFX gives you:

- a cleaner UI model than Swing
- observable properties and bindings
- better separation between UI and state
- easier async work coordination with `Task` and `Service`
- a cleaner base for future features like zoom, minimaps, markers, thumbnails, and richer previews

### Why Keep `javax.sound.sampled`

The app does not need a media framework with video decoding or advanced DSP. It needs:

- reliable WAV playback
- large-file handling
- precise timing
- control over seek/play/pause/position

`javax.sound.sampled` is still a reasonable choice if used correctly:

- `Clip` for small/preloaded audio
- `SourceDataLine` for streaming long files

The current project already uses Java Sound, so keeping that domain avoids unnecessary complexity.

### Why `Canvas` for the Timeline

The waveform/timeline editor should not be built as thousands of scene-graph nodes. That would become inefficient and harder to control.

`Canvas` is the right tool for:

- waveform drawing
- cursor drawing
- timing boundaries
- overlays
- selection highlights
- zoomed rendering
- cached image strips

Use the JavaFX scene graph for:

- controls
- panels
- buttons
- forms
- lyric editor containers
- preview layout

Use `Canvas` for:

- timeline drawing
- waveform drawing
- paint markers
- guide lines
- playback cursor

### Why `jpackage`

This app should ship as a self-contained desktop application. Users should not need to install a JRE manually.

`jpackage` gives you:

- bundled runtime
- platform-native app bundles
- cleaner deployment
- fewer support issues

---

## Recommended End-State Architecture

Use a multi-module Gradle project.

### Module Layout

```text
ngp-karaoke/
  settings.gradle
  build.gradle
  gradle.properties

  app-desktop/
    build.gradle
    src/main/java/...
    src/main/resources/...

  ui-javafx/
    build.gradle
    src/main/java/...
    src/main/resources/...

  audio-engine/
    build.gradle
    src/main/java/...

  core-domain/
    build.gradle
    src/main/java/...

  core-io/
    build.gradle
    src/main/java/...
    src/test/java/...

  core-edit/
    build.gradle
    src/main/java/...
    src/test/java/...

  core-timing/
    build.gradle
    src/main/java/...
    src/test/java/...

  test-fixtures/
    io/
```

### Responsibilities

#### `core-domain`

Owns:

- `Project`
- `TrackAudio`
- `LyricLine`
- `LyricWord`
- timing segments
- selection model
- editor state model

Must not depend on:

- JavaFX
- Swing
- Java Sound

#### `core-timing`

Owns:

- timing conversion
- timeline units
- export timing conversion
- seek offset logic
- duration formatting helpers if still needed

Must remain deterministic and heavily tested.

#### `core-edit`

Owns:

- split word
- merge word
- cut painted word
- undo cut
- row split
- row merge
- repair logic

#### `core-io`

Owns:

- `.ngp` read/write
- export file generation
- fixture-based regression tests

#### `audio-engine`

Owns:

- playback transport
- streaming playback
- waveform analysis
- waveform cache generation
- playback cursor timing
- seek APIs

Must not depend on:

- JavaFX scene graph classes

It may expose:

- plain Java listeners
- immutable snapshots
- observable state wrappers at the app layer

#### `ui-javafx`

Owns:

- JavaFX scenes
- controllers or view models
- canvas renderer
- bindings to app state

Must not own:

- file format logic
- low-level audio parsing
- domain mutation rules

#### `app-desktop`

Owns:

- application bootstrap
- dependency wiring
- menus
- application commands
- packaging entry points

---

## Current Code to Preserve

Do not throw away the following logic. These are already useful refactor assets.

### Keep and Migrate

- `NgpProjectReader`
- `NgpProjectWriter`
- `Ng1ExportWriter`
- `TimelineMath`
- `RowEditService`
- `WordTextEditService`
- `ProjectRepairService`
- `LyricImportService`
- `ProjectSaveService`
- `ProjectSession`
- `ProjectSessionService`
- `PaintWorkflowService`
- `SelectionNavigationService`
- `WordModel`
- `WordVisualState`

### Transitional Only

These should not survive as-is in the final JavaFX architecture:

- `KaraokeCreatorLast`
- `WavPlayer`
- `VideoPanel`
- `wordLine`
- `myTextfield`

---

## Migration Principles

### Principle 1: Preserve Business Logic, Replace UI

The app's business logic is more important than the current UI code.

Preserve:

- project semantics
- edit semantics
- timing semantics
- export semantics
- keyboard workflow semantics

Replace:

- Swing windowing
- manual widget wiring
- ad hoc timeline rendering
- audio/UI coupling

### Principle 2: Move by Vertical Slices

Do not migrate by file.

Migrate by feature slice:

1. project open/save
2. lyric import
3. timeline display
4. playback transport
5. paint workflow
6. export

### Principle 3: Keep the Current App Working During Migration

Do not delete the current app until the JavaFX app can:

- open WAV
- open/import text
- load `.ngp`
- paint timings
- edit words/rows
- save `.ngp`
- export karaoke output

### Principle 4: Standardize the Domain First

Before the JavaFX UI is too far along, complete the move away from `wordLine` as a central mutable wrapper.

The target should be:

- domain objects in `core-domain`
- UI adapters in `ui-javafx`

---

## Proposed Target UI

### Main Screen Areas

1. Top toolbar
2. Left or center timeline editor
3. Right preview area
4. Bottom or side lyric editor
5. Status strip

### Main Views

#### Project Toolbar

Contains:

- import audio
- import text
- open project
- save project
- export
- play/pause
- start/stop paint
- zoom controls

#### Timeline Editor

JavaFX `Canvas` plus scroll/zoom support.

Draw:

- waveform
- playhead
- selection
- line start/end markers
- word timing bars
- end-line markers
- hover indicator
- drag handles later if added

#### Lyric Editor

Do not build this as one `TextField` per word unless performance remains acceptable.

Candidate approaches:

- virtualized line list with custom cells
- line-based editor with word chips
- hybrid model using a `ListView` of lines and custom line renderers

The current app's one-textfield-per-word pattern should be treated as legacy.

#### Preview Area

Use JavaFX text rendering or a `Canvas`.

The preview should depend on:

- playback position
- selected lines
- word paint state

It should not query low-level audio classes directly.

---

## Audio Engine Design

### Problem With the Current Audio Design

The current `WavPlayer` mixes:

- UI rendering
- playback control
- waveform extraction
- seek logic
- timing mutation

That coupling must end before the new app is considered maintainable.

### Target Audio Components

#### `AudioPlaybackService`

Responsibilities:

- open audio source
- play
- pause
- stop
- seek
- current position
- duration
- playback state

Public state:

- `isReady`
- `isPlaying`
- `positionMillis`
- `durationMillis`

Long-file playback:

- use `SourceDataLine`
- decode in a worker thread
- push PCM buffers to the line

#### `WaveformAnalysisService`

Responsibilities:

- scan WAV
- compute peaks
- cache reduced waveform
- optionally build multi-resolution waveform levels for zooming

Outputs:

- overview peaks
- zoomed segment peaks

#### `PlaybackClock`

Responsibilities:

- authoritative playback timeline position
- convert between audio frame position and editor timeline units

This must be the single truth for playback position.

### Why `SourceDataLine`

For long files, preloading the entire audio into `Clip` is the wrong model.

Use `Clip` only if:

- you explicitly want a small-file fast path

Use `SourceDataLine` for:

- 10+ minute WAV
- larger files
- predictable streaming behavior
- future caching strategies

### Audio Engine Rules

- audio decoding must never run on the JavaFX application thread
- waveform analysis must never run on the JavaFX application thread
- UI updates must happen on the JavaFX application thread
- the playback service must be independently testable from the UI

---

## JavaFX Concurrency Model

Use `javafx.concurrent.Task` or `Service` for:

- open project
- import text
- load audio
- waveform analysis
- export
- repair batch jobs

Use `Platform.runLater` only for small UI updates.

Do not put business logic inside UI thread callbacks.

### Good Pattern

- service starts background task
- task returns a result object
- UI binds to task state
- success handler updates app state

### Bad Pattern

- file parsing inside button click handler
- waveform generation inside canvas redraw
- playback state mutation directly inside view code

---

## State Management

### Current Direction

`ProjectSession` already gives a useful place for editor session state.

### Target Direction

Split state into:

#### `ProjectState`

- loaded project
- current file paths
- save dirty flag

#### `EditorState`

- selected word
- selected line
- current tool mode
- paint mode
- zoom level
- viewport position

#### `PlaybackState`

- ready/loading/error
- playing/paused/stopped
- current position
- duration

In JavaFX, expose these as properties or view model fields.

---

## Detailed Migration Phases

## Phase 0: Freeze Current Behavior

Before the JavaFX migration starts:

1. Keep the current Swing app buildable.
2. Keep the current jar runnable.
3. Preserve all current fixture/verifier programs.
4. Document current keyboard behavior.
5. Record manual regression steps.

### Exit Criteria

- current app builds
- current jar runs
- current verifier suite passes

---

## Phase 1: Introduce Gradle

### Goals

- modern build
- Java 21 toolchain support
- reproducible packaging

### Tasks

1. Create single-project Gradle build first.
2. Set Java toolchain to 21.
3. Add source sets matching the current structure.
4. Add tasks:
   - `build`
   - `test`
   - `verifyFixtures`
   - `packageApp`
5. Move verifier classes under a dedicated verification task if not yet JUnit-based.

### Recommendation

Do not move to multi-module immediately if that slows momentum.

Possible path:

1. single Gradle project
2. migrate code
3. split into modules after build is stable

### Exit Criteria

- app builds with Gradle
- verifiers run with Gradle
- Java 21 toolchain is active

---

## Phase 2: Introduce Proper Automated Tests

### Goals

- reduce migration risk
- lock down core behavior

### Tasks

1. Convert verifier programs into JUnit tests where practical.
2. Keep fixture-based I/O tests.
3. Add regression tests for:
   - `.ngp` read/write roundtrip
   - export generation
   - timing conversion
   - split/merge behavior
   - row edits
   - paint workflow rules
   - selection navigation rules

### Must-Have Coverage

- project open/save
- export
- timing math
- edit services

### Nice-to-Have Coverage

- waveform analysis result size and stability
- audio seek mapping

### Exit Criteria

- regression suite runs in CI or local build
- core logic covered without GUI

---

## Phase 3: Finalize the Domain Boundary

### Goals

- stop carrying legacy Swing-bound model assumptions

### Tasks

1. Introduce proper domain classes:
   - `Project`
   - `LyricLine`
   - `LyricWord`
   - `ProjectMetadata`
2. Make `wordLine` a compatibility adapter only.
3. Move all mutable timing/edit logic onto domain services.
4. Remove direct UI widget references from domain-ish objects.

### Exit Criteria

- domain classes do not depend on Swing
- project logic does not require `wordLine`

---

## Phase 4: Build the New Audio Engine

### Goals

- robust long-file playback
- maintainable audio state

### Tasks

1. Build `AudioPlaybackService`.
2. Build `WaveformAnalysisService`.
3. Support:
   - open
   - play
   - pause
   - stop
   - seek
   - current position
   - duration
4. Add tests for:
   - seek conversion
   - duration
   - state transitions

### Important Design Rule

The new audio engine must not know about:

- JavaFX controls
- lyric text fields
- timeline canvas

It may publish position/state to the app layer.

### Exit Criteria

- long WAVs play reliably
- seek is stable
- playback state is testable

---

## Phase 5: Build the JavaFX Skeleton App

### Goals

- establish the new runtime shell

### Tasks

1. Create JavaFX app bootstrap.
2. Add:
   - main stage
   - menu
   - toolbar
   - empty timeline area
   - empty preview area
   - empty lyric editor area
3. Wire menu actions to placeholder services.

### Exit Criteria

- JavaFX app launches
- basic layout exists
- actions are wired

---

## Phase 6: Implement Timeline Canvas

### Goals

- replace `WavPlayer` UI responsibilities

### Tasks

1. Build `TimelineCanvas`.
2. Render:
   - waveform
   - playhead
   - word markers
   - line endings
   - selection
3. Add:
   - scroll
   - zoom
   - click-to-seek
   - hover state

### Stretch Goal

Add multi-resolution waveform rendering:

- overview cache
- zoomed-in cache

### Exit Criteria

- waveform visible
- seeking works
- selection maps correctly

---

## Phase 7: Implement Lyric Editor in JavaFX

### Goals

- replace Swing word field editor

### Tasks

1. Choose the UI model:
   - line list with custom renderer
   - virtualized editor
2. Bind selection to `EditorState`.
3. Support:
   - edit word
   - split word
   - merge word
   - row split
   - row merge
   - painted cut/undo

### Important Rule

UI actions must call services. They must not reimplement edit logic.

### Exit Criteria

- all main edit actions work in JavaFX

---

## Phase 8: Implement Paint Workflow

### Goals

- reproduce the current keyboard-driven timing workflow

### Tasks

1. Bind keyboard actions in JavaFX.
2. Recreate:
   - `F` start/stop paint
   - `R` mark word
   - `E` mark line end
   - `T` undo paint
   - arrows select/nudge
   - `W` reset paint cursor
3. Ensure selection and playback stay synchronized.

### Exit Criteria

- paint mode works end-to-end in JavaFX

---

## Phase 9: Rebuild Preview

### Goals

- replace `VideoPanel`

### Tasks

1. Render active lines and current word progression.
2. Bind preview to playback state and selected timing state.
3. Support configurable fonts, colors, and background.

### Exit Criteria

- preview reflects playback correctly

---

## Phase 10: Save, Open, Export

### Goals

- complete end-to-end workflow

### Tasks

1. Use existing core I/O services.
2. Implement JavaFX file dialogs.
3. Support:
   - import WAV
   - import TXT
   - open `.ngp`
   - save `.ngp`
   - export karaoke file

### Exit Criteria

- full workflow is available in the JavaFX app

---

## Phase 11: Packaging

### Goals

- ship a real desktop app

### Tasks

1. configure runtime image creation
2. configure `jpackage`
3. generate:
   - Windows installer or app image
   - macOS app image or dmg
4. set:
   - app icon
   - app name
   - version
   - vendor

### Exit Criteria

- self-contained native package is built locally

---

## Migration Order Recommendation

Use this exact order:

1. Gradle
2. tests
3. domain boundary cleanup
4. audio engine
5. JavaFX shell
6. timeline canvas
7. lyric editor
8. paint workflow
9. preview
10. save/open/export
11. packaging

This order minimizes risk.

---

## What Not To Do

### Do Not Rewrite Domain Logic Inside JavaFX Controllers

If controllers start reimplementing split/merge/export behavior, the migration will fail.

### Do Not Keep `WavPlayer` and Wrap It Forever

Temporary embedding is acceptable only as a bridge. It should not become permanent technical debt.

### Do Not Start With Visual Restyling

The first success criterion is functional parity, not visual redesign.

### Do Not Migrate and Refactor Everything in One Commit

Keep phases small and reversible.

---

## Manual Regression Checklist

Before cutting over to JavaFX as the primary app, test:

1. open a 10+ minute WAV
2. waveform appears
3. click-to-seek works
4. play/pause works
5. import TXT works
6. load `.ngp` works
7. `F` starts paint mode
8. `R` marks current word
9. `E` marks line end
10. `T` undoes last painted word
11. row split/merge works
12. word split/merge works
13. save `.ngp` works
14. export works
15. reopen saved project and verify timings remain stable

---

## Suggested Feature Additions After Migration

These become much more realistic after the new architecture is in place:

- waveform zoom levels
- minimap overview
- marker tracks
- snap-to-grid
- undo/redo stack
- autosave
- better preview styling
- keyboard shortcut editor
- recent projects
- project validation panel
- export profiles
- batch export
- timeline drag handles
- multi-select edits

---

## Readiness for Java 21 Migration

You can consider the app ready to start Java 21 migration when:

- the current Java 8 app still builds
- verifier suite passes
- Gradle is in place or scheduled as the immediate next step
- the team agrees to preserve core services and replace the UI layer

You can consider the new JavaFX app ready to replace the old app when:

- all core workflows work
- large WAV handling is stable
- save/open/export roundtrip is validated
- packaged app runs without local JRE setup

---

## Immediate Next Task

The best next implementation step is:

1. create Gradle build files
2. move current verifier programs into a Gradle verification workflow
3. start a new JavaFX app module with an empty shell

That is the cleanest bridge from the current refactored Swing app to the future Java 21 JavaFX application.
