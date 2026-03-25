# AGENTS.md

## Purpose

This file is the working guide for agents refactoring and modernizing this repository.

This repository now contains a single legacy Java Swing karaoke authoring application. The goal is to improve structure, stability, and maintainability without breaking the production workflow.

## Current Source Layout

### Main app code

- `src/karaoke/app/main`
  Main application entry points and editor workflow
- `src/karaoke/app/main/source`
  Main app audio/timeline and preview widgets

### Shared code

- `src/karaoke/shared`
  Shared models, enums, widgets, dialogs, and resource bundle

### Assets

- `image/`
  Runtime image assets used directly by the Swing UI

### Output artifacts

- `Karaoke creator.jar`
  Main app jar
- `bin/`
  Eclipse-style compiled output

## Product Overview

The main app:

- loads WAV audio
- loads or types lyrics
- splits lyrics into word units
- allows editing of word/line structure
- allows timing each word during playback
- previews karaoke highlighting
- saves project files
- exports karaoke timing text

Entry point:

- `karaoke.app.main.KaraokeCreatorLast`

## Core Architectural Reality

The real center of the system today is `wordLine`.

`wordLine` currently mixes:

- word/domain state
- timing state
- line-role state
- split state
- preview rendering state
- waveform rendering state
- Swing text field state

This is the biggest design problem in the repository.

The other major issue is that `KaraokeCreatorLast` owns too much behavior:

- UI actions
- keyboard handling
- parsing
- export
- edit rules
- timing rules
- repair tools
- workflow state

## Primary Refactor Goal

Separate the codebase into clean layers without changing user-visible behavior unless intentionally requested.

Target long-term separation:

- domain model
- persistence
- export
- audio services
- preview rendering
- UI widgets
- application workflows

## Non-Negotiable Rules

### 1. Preserve workflow first

Do not casually change:

- file formats
- keyboard shortcuts
- timing semantics
- split/merge behavior
- row split/merge behavior
- export conventions

If behavior must change, document it explicitly.

### 2. Keep refactors incremental

Never attempt a full rewrite in one pass.

Preferred pattern:

1. extract logic
2. keep adapters to old call sites
3. verify build and behavior
4. then reduce old code

### 3. Separate by ownership

Keep code in the correct area:

- main app only code stays under `app/main`
- genuinely shared code stays under `shared`

Do not move code into `shared` just because multiple classes use it. Only share what is actually reusable and stable.

### 4. Avoid breaking jar entry points unless intentionally updating packaging

Current jar entry point:

- main: `karaoke.app.main.KaraokeCreatorLast`

If the entry point changes, update jar packaging and verify launch behavior.

### 5. Java compatibility

Current deliverables should remain Java 8 compatible unless the modernization phase explicitly changes the runtime baseline.

When rebuilding the jar during refactor, compile with Java 8 target compatibility.

Recommended compile mode:

```sh
javac --release 8 ...
```

## Current Known Good Constraints

### Packaging and runtime

- jar should stay Java 8 compatible for now
- resource bundle path is `karaoke/shared/admin`

### Main app jar

- must launch
- must open large WAV files
- must still allow marking timings
- must still save/open `.ngp`
- must still export main karaoke output

## Refactor Priorities

Refactor in this order unless there is a strong reason to deviate.

### Priority 1: Persistence extraction

Extract parsing and writing from UI classes.

Create dedicated classes for:

- project readers
- project writers
- export writers

Suggested future package:

- `src/karaoke/shared/io`

Suggested class names:

- `NgpProjectReader`
- `NgpProjectWriter`
- `Ng1ExportWriter`

Why first:

- highest leverage
- lowest UI risk
- easiest to test

### Priority 2: Timing conversion extraction

Centralize timing conversion rules.

Current code uses magic constants and spread-out calculations.

Create a dedicated abstraction for:

- internal time unit
- playback/frame unit
- export unit

Suggested future classes:

- `TimelineMath`
- `TimingConverter`

### Priority 3: Edit-operation extraction

Move split/merge and row-edit logic into services.

Suggested services:

- `WordSplitService`
- `WordMergeService`
- `RowEditService`
- `TimingEditService`

This logic should become callable without Swing widgets.

### Priority 4: Reduce `wordLine`

Split `wordLine` into:

- pure data model
- UI wrapper or view binding

Do not do this first unless absolutely necessary. It touches a lot of code.

### Priority 5: Reduce god classes

After the above logic is extracted, shrink:

- `KaraokeCreatorLast`

The goal is for that class to coordinate workflow, not own all business logic.

## Safe Refactor Pattern

When extracting logic from a legacy class, use this pattern:

1. identify one cohesive behavior
2. move it into a new class with minimal API
3. keep the old caller behavior the same
4. wire the old class to call the new class
5. compile
6. manually verify workflow
7. only then remove duplicated old logic

Do not combine multiple risky extractions in one patch unless they are tightly related.

## Domain Invariants To Preserve

These rules must not change accidentally.

### Word order matters

Word index order drives:

- selection
- timing
- preview
- export

### Line index matters

Line index affects:

- row layout
- preview display
- export structure
- line merge/split behavior

### Split state matters

Word split behavior is not cosmetic.

The following are significant:

- `normal`
- `splite_main`
- `splite_sub`
- `splite_sub_end`

Any refactor must preserve the semantics of these states.

### Location matters

`Location` values:

- `start`
- `middle`
- `end`

These affect:

- timing movement
- row boundaries
- preview transitions
- export

### Export compatibility matters

Do not silently alter output formatting for:

- `.ngp`
- `.ng1`
- `cr.txt`
- helper text files still used by the main app

Unless export versioning is introduced intentionally.

## High-Risk Areas

These areas need extra care during refactor.

### 1. `wordLine`

Very high coupling.

Any change here can impact:

- parsing
- editing
- rendering
- preview
- export

### 2. `KaraokeCreatorLast`

Main workflow state is concentrated here.

Changes can break:

- timing workflow
- keyboard handling
- project open/save
- export

### 3. `WavPlayer`

This class is both:

- audio playback service
- timeline widget

Long-audio support was already improved. Do not regress:

- background loading
- waveform generation
- paint-mode playback behavior

### 4. Project load/open normalization

Some load paths repair or normalize data while reading.

Be careful not to remove production repair behavior accidentally.

## Low-Value or Legacy Areas

These areas are candidates for cleanup, but only after confirming they are unused.

### `PaintCharPanel`

Likely legacy or unused in the active workflow.

### old/commented code paths

There are many commented sections and alternative export flows. Remove them only after verifying they are not still relied on operationally.

## Resource Handling Rules

### Resource bundle

Current resource bundle base path:

```java
ResourceBundle.getBundle("karaoke/shared/admin", locale)
```

### Images

Images are loaded via relative file paths like:

- `image/iconJ.png`
- `image/audioPlay.png`

Do not move or rename image assets casually unless also updating all references.

## Build and Verification

### Minimum compile verification

Use:

```sh
javac --release 8 -d /tmp/build $(find src -name '*.java')
```

### Main jar packaging

Main class:

```text
karaoke.app.main.KaraokeCreatorLast
```

### Minimum manual checks after meaningful refactor

For main app:

- app launches
- open WAV works
- open/import lyrics works
- paint mode works
- save/open project works
- export works

## Refactor Boundaries

### Main-app-only candidates

Usually belongs under `app/main` or `app/main/source`:

- main editor workflows
- audio playback/timeline widget
- karaoke preview rendering
- main-app keymap
- main-app export UI

### Shared candidates

Only keep under `shared` if truly shared and stable:

- enums
- reusable widgets
- common model types
- common parsers/writers
- shared validation logic
- shared resource bundle

## What Agents Should Do First

If starting active refactor work, begin with:

### Step 1

Extract project read/write logic into standalone classes.

### Step 2

Add fixture-based verification for:

- project load
- project save
- export generation

### Step 3

Extract timing conversion logic.

Only after those are stable should agents begin decomposing `wordLine`.

## What Agents Should Avoid

- large-scale renaming without structural value
- UI rewrites before logic extraction
- changing shortcut behavior casually
- changing file formats casually
- mixing Java-runtime migration with deep logic refactor in the same change set
- deleting shared logic without first confirming the main app still uses it

## Future End State

The desired end state is:

- pure domain models
- dedicated persistence layer
- dedicated export layer
- dedicated audio services
- thinner UI controllers
- safer incremental feature development

At that point, the team can choose between:

- keeping Swing and modernizing internally
- or rebuilding the UI on top of a clean architecture

## Short Summary For Agents

If you are modifying this repo:

- preserve behavior
- refactor in small slices
- keep Java 8 compatibility for now
- separate shared logic from main-app-specific logic
- extract persistence first
- treat `wordLine`, `KaraokeCreatorLast`, and `WavPlayer` as high-risk files
- verify the main jar still builds and launches
