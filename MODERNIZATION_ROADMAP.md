# Karaoke Creator Modernization Roadmap

## Goal

Modernize the karaoke application without losing the production workflow that already works.

The correct strategy is:

1. stabilize the current application
2. refactor the existing code into cleaner modules
3. preserve behavior with regression coverage
4. improve deployment and platform baseline
5. add features on top of the cleaner structure
6. only rebuild major UI pieces if the current Swing app becomes the bottleneck

This is not a rewrite-first plan.

## Guiding Principles

- preserve the current authoring workflow first
- do not lose edge-case business logic hidden in the old code
- separate model, UI, audio, and persistence before adding major features
- keep each phase shippable
- reduce risk by moving logic, not reimagining it all at once
- document file formats and timing semantics before changing them

## Current State Summary

The application is useful, but heavily coupled.

Main issues:

- `KaraokeCreatorLast` is a god class
- `wordLine` mixes domain state and Swing UI state
- parsing and export are handwritten and scattered
- audio logic, UI logic, and rendering logic are mixed
- Windows path assumptions are hardcoded
- there is almost no automated regression safety

Main strength:

- the user workflow is real and proven

That means the best modernization strategy is refactor-first, rewrite-later-if-needed.

## Overall Plan

### Phase 0: Baseline and Safety

Purpose:

- understand the system completely
- lock in current behavior
- create enough safety to refactor confidently

### Phase 1: Architecture Extraction

Purpose:

- separate logic from the UI
- reduce coupling
- make code understandable and testable

### Phase 2: Stability and Quality Upgrade

Purpose:

- harden parsing, export, timing, and error handling
- remove legacy debt
- improve maintainability

### Phase 3: Platform Modernization

Purpose:

- move from Java 8 to modern Java LTS
- improve packaging and runtime consistency

### Phase 4: Feature Expansion

Purpose:

- add new user-facing features on a clean foundation

### Phase 5: Optional UI Rebuild

Purpose:

- only rebuild the UI if Swing becomes the limiting factor

## Phase 0: Baseline and Safety

### Objectives

- document current behavior
- freeze current file formats
- identify all keyboard and context-menu workflows
- verify which legacy utilities are still operationally required
- create a minimal regression harness

### Deliverables

- architecture review
- modernization roadmap
- current keymap and workflow reference
- file format reference for `.ngp`, `.ncp`, `.ng1`, `.nc1`, `cr.txt`, `crill.txt`, `mon.txt`
- sample project/audio/text fixtures

### Tasks

- inventory all file formats and fields
- document timing units and conversions
- capture expected behavior for:
  - import text
  - split/merge word
  - split/merge row
  - paint timing
  - save/open project
  - export outputs
- identify dead code versus operational repair tools

### Test Targets

At minimum:

- project file roundtrip
- export output generation
- split and merge rules
- row split and merge rules
- timing move logic

### Why This Matters

Without this phase, refactoring will accidentally remove hidden business rules.

## Phase 1: Architecture Extraction

This is the most important phase.

### Main Goal

Move from event-handler-driven spaghetti to a structure with clear ownership.

### Target Modules

Recommended package structure:

- `karaoke.model`
- `karaoke.service`
- `karaoke.io`
- `karaoke.ui`
- `karaoke.preview`
- `karaoke.audio`
- `karaoke.export`
- `karaoke.tools`

### 1. Extract Pure Domain Models

Create plain model objects with no Swing dependencies.

Recommended classes:

- `KaraokeProject`
- `SongMetadata`
- `LyricLine`
- `LyricWord`
- `WordTiming`
- `SplitType`
- `LinePosition`

The current `wordLine` should eventually become:

- a pure domain object
- plus a separate view-model or UI wrapper

### 2. Extract Persistence Layer

Create dedicated parser/writer classes:

- `NgpProjectReader`
- `NgpProjectWriter`
- `NcpProjectReader`
- `NcpProjectWriter`
- `Ng1ExportWriter`
- `Nc1ExportWriter`
- `CrTextExportWriter`

These classes should own:

- parsing
- validation
- serialization
- migration/repair rules if still needed

### 3. Extract Audio Service Layer

Move long-running and low-level audio logic out of the widget.

Suggested classes:

- `AudioPlaybackService`
- `WaveformGenerator`
- `WaveformCache`
- `PlaybackPosition`

The UI widget should display state, not own the whole lifecycle.

### 4. Extract Editing Operations

Create explicit commands or services for domain mutations:

- `WordSplitService`
- `WordMergeService`
- `RowEditService`
- `TimingEditService`
- `ProjectRepairService`

This is a high-value change because these rules are currently buried in UI code.

### 5. Extract Keyboard Mapping

Keyboard actions should be centralized in a command map, not embedded in one huge switch.

Suggested approach:

- `EditorKeymap`
- `EditorCommand`
- `EditorActionDispatcher`

### 6. Extract Preview State

The preview should consume view data, not mutate word state directly.

Suggested classes:

- `PreviewFrameState`
- `PreviewLineState`
- `PreviewWordState`

### Phase 1 Success Criteria

- `KaraokeCreatorLast` becomes much smaller
- `wordLine` is reduced or replaced
- file parsing/writing is isolated
- audio logic is not tightly tied to the panel
- edit operations are testable without the UI

## Phase 2: Stability and Quality Upgrade

### Objectives

- make the app more reliable
- remove legacy confusion
- standardize internal rules

### 1. Formalize Time Units

Today the app uses hidden conversion constants.

This phase should define:

- internal authoring time unit
- playback time unit
- export time unit
- conversion helpers

Create a single utility or value object for this.

Suggested classes:

- `TimeUnitConverter`
- `TimelinePosition`
- `WordDuration`

### 2. Centralize Validation

Validate:

- malformed project files
- inconsistent line ids
- negative or overlapping timing
- invalid split states
- missing adjacent files

Suggested classes:

- `ProjectValidator`
- `ExportValidator`

### 3. Replace Hardcoded Paths

Remove assumptions like:

- `D:\\budalt`
- `D:\\LOG.txt`

Replace with:

- last-used directory
- user home
- configurable settings

### 4. Replace stdout Debugging

Remove `System.out.println` debugging and replace with:

- a lightweight logger
- structured debug messages
- optional debug mode

### 5. Clean Up Dead Code

Likely targets:

- unused old export paths
- old experimental preview components
- disabled licensing code
- commented-out feature fragments

### 6. Add Regression Tests

By the end of this phase, the highest-risk business rules should be covered.

### Phase 2 Success Criteria

- project open/save/export are deterministic
- timing logic is documented and centralized
- dead code is reduced
- platform assumptions are minimized
- the code is easier to debug

## Phase 3: Platform Modernization

### Recommendation

Move from Java 8 to Java 21 LTS after the architecture extraction is stable.

### Why Not Stay on Java 8 Forever

- older baseline
- weaker tooling
- weaker packaging options
- no meaningful benefit for long-term maintenance

### Why Not Jump Immediately

- migration alone does not solve architecture
- mixing platform migration with logic refactor increases risk

### Phase 3 Tasks

- compile and run on Java 21 LTS
- clean up deprecated APIs
- modernize packaging
- decide whether to ship a bundled runtime

### Good Outcome

- users no longer need to manage the correct JRE manually
- the app runs on a controlled runtime
- build and packaging become more predictable

## Phase 4: Feature Expansion

Only begin this phase after Phase 1 and most of Phase 2 are done.

### High-Value Features

#### 1. Better audio timeline UX

- zoom in and out on waveform
- jump to exact time
- loop selection
- snap-to-word timing guides
- show current paint index clearly

#### 2. Better project management

- recent projects list
- autosave
- backup snapshots
- crash recovery

#### 3. Better lyric editing

- bulk replace
- punctuation-aware word splitting
- line normalization tools
- multi-select edits

#### 4. Better export support

- export presets
- validation before export
- preview of export structure
- import/export compatibility tools

#### 5. Better alternate-script workflow

- side-by-side synchronized lyric editing
- transliteration helper tools
- alternate-script validation

#### 6. Better operational tools

- batch project repair as a standalone utility
- batch export mode
- project diagnostics

### Medium-Value Features

- undo/redo stack
- customizable keyboard shortcuts
- theme settings
- adjustable preview font, spacing, color presets
- multiple background preview styles

### Lower-Priority Features

- advanced skins
- flashy animations
- new visuals without workflow gains

## Phase 5: Optional UI Rebuild

This phase is optional.

Do this only if:

- Swing becomes a real development bottleneck
- you need a dramatically improved UX
- packaging strategy supports a larger change

### When To Keep Swing

Keep Swing if:

- the workflow is stable
- the app is primarily internal
- most value comes from logic improvements rather than visual redesign

### When To Rebuild UI

Consider rebuilding if:

- you want docking layouts
- richer waveform interactions
- easier scaling for complex editing views
- modern OS-native packaging experience

### Important Rule

If a UI rebuild happens, it should sit on top of the refactored domain/service layers.

Do not rebuild the UI on top of the old architecture.

## Suggested Execution Order

### Sprint Group 1

- formalize current behavior
- document formats and shortcuts
- add sample fixtures
- create baseline parser/export tests

### Sprint Group 2

- extract project parsing/writing
- extract export logic
- extract timing conversion logic

### Sprint Group 3

- extract word split/merge and row edit services
- reduce `KaraokeCreatorLast`
- reduce `wordLine` responsibilities

### Sprint Group 4

- extract audio services
- isolate waveform generation and playback state
- harden error handling

### Sprint Group 5

- remove dead code
- remove hardcoded Windows assumptions
- finalize regression coverage

### Sprint Group 6

- migrate to Java 21
- improve packaging
- add selected high-value features

## Risk Management

### Biggest Risk

The biggest risk is not technical migration. It is losing hidden production logic during refactor.

### Risk Controls

- keep changes incremental
- keep old behavior documented
- test with real sample project files
- preserve export compatibility unless intentionally versioned
- refactor by extraction first, not by redesign first

## What We Should Preserve

- word-based timing workflow
- row and split semantics
- fast keyboard-driven editing
- karaoke preview concept
- repair tools users actually depend on

## What We Should Replace

- god classes
- UI-driven business logic
- handwritten parsing scattered across classes
- platform-specific path assumptions
- hidden timing conversion constants
- dead and duplicate code paths

## Concrete Recommendation

Yes, we should refactor this code first and then move on to rebuilding selectively.

The modernization sequence should be:

1. refactor the current codebase
2. add regression safety
3. stabilize file formats and timing logic
4. migrate to modern Java
5. add features
6. rebuild UI only if still necessary

That is the lowest-risk and highest-value path.

## Immediate Next Step

The next best engineering step is:

- start Phase 1 by extracting persistence and domain logic from `KaraokeCreatorLast` and `wordLine`

That gives the best leverage for every later improvement.
