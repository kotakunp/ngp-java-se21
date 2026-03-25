# Karaoke Creator App Review

## Purpose

This project is a desktop karaoke authoring tool written in Java Swing.

Its main job is:

- load a WAV audio file
- load or edit lyrics
- split lyrics into word-level units
- assign timing to each word while listening to the song
- preview karaoke-style highlighting
- save an editable project
- export custom timing/text files for downstream use
- optionally maintain an alternate Mongolian-script version of the same lyrics

The application is not a general media player. It is a specialized word-timing editor for karaoke production.

## Technology Baseline

- Language: Java
- Target runtime in project metadata: Java SE 8
- UI toolkit: Swing
- Audio API: `javax.sound.sampled`
- Build style: old Eclipse Java project, no Maven or Gradle
- Packaging: manually built `.jar` files
- External dependencies: none

Project metadata confirms Java 8:

- [.classpath](/Users/kotakunp/Desktop/ngp-jar/.classpath)
- [.project](/Users/kotakunp/Desktop/ngp-jar/.project)

## Shipped Applications

There are two packaged entry points:

- `Karaoke creator.jar`
  Main class: `karaoke.main.KaraokeCreatorLast`
- `Karaoke creator MB.jar`
  Main class: `karaoke.main.MongolBichigCreator`

These are two related desktop tools built from the same source tree.

## High-Level Architecture

The architecture is simple but tightly coupled.

### Main layers as they exist today

1. Window shell and shared UI widgets
   - [KaraokeCreator.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreator.java)

2. Main application behavior
   - [KaraokeCreatorLast.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreatorLast.java)

3. Alternate Mongolian-script editor
   - [MongolBichigCreator.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/MongolBichigCreator.java)

4. Audio timeline editor and waveform renderer
   - [WavPlayer.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/WavPlayer.java)

5. Karaoke preview renderer
   - [VideoPanel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/VideoPanel.java)

6. Word-level domain object plus embedded UI state
   - [wordLine.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/wordLine.java)

7. Small wrapper UI components
   - [MyButton.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyButton.java)
   - [MyLabel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyLabel.java)
   - [MyMeniItem.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyMeniItem.java)
   - [myTextfield.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/myTextfield.java)
   - [MyComfirmDialog.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyComfirmDialog.java)

## Runtime Model

The entire application revolves around a mutable list of `wordLine` objects.

Each `wordLine` represents one logical lyric word and contains:

- text content
- optional Mongolian-script content
- word index
- row index
- start timing
- end timing
- split state
- line-break role
- paint/play/highlight state
- color state
- waveform/timeline drawing data
- video preview drawing data
- a live Swing `JTextField` instance

This object is the true center of the app.

### Why this matters

This design makes the app easy to build quickly, but hard to maintain:

- the domain model is not separate from the UI
- serialization logic is mixed with editing logic
- timing logic is mixed with rendering logic
- state can be changed from many places without a clear owner

## Main User Workflow

The primary application flow is implemented in [KaraokeCreatorLast.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreatorLast.java).

### 1. Open or import audio

The app loads a `.wav` file and prepares:

- playback
- waveform
- seek/timeline drawing
- duration display

Relevant code:

- [KaraokeCreatorLast.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreatorLast.java)
- [WavPlayer.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/WavPlayer.java)

### 2. Import or type lyrics

Lyrics can come from:

- a plain `.txt` file
- direct text input dialog
- an existing project file

The text is split on spaces and converted into one `wordLine` per word.

### 3. Edit text structure

The user can:

- edit a word
- split a word
- merge words
- split a line
- merge a line
- reassign rows

This is exposed through right-click context menus and keyboard shortcuts.

### 4. Paint timings

This is the core custom behavior.

The user starts paint mode and then marks timing word by word while the song plays.

The app stores per-word timing, line-break positions, and derived duration values.

### 5. Preview karaoke effect

The preview panel renders two rows at a time and progressively fills words to simulate karaoke highlighting.

### 6. Save project and export

The app can:

- save editable project data
- export karaoke timing text
- export helper text files
- export alternate script variants

## Main Windows and UI Regions

### Base window

Defined in [KaraokeCreator.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreator.java).

It builds:

- menu bar
- toolbar
- preview area
- lyrics editing area
- audio/timeline panel
- seek adjustment dialog

### Audio panel

Handled by [WavPlayer.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/WavPlayer.java).

It is responsible for:

- audio playback
- vertical waveform rendering
- current playback cursor
- selecting timeline position with mouse
- nudging word timings
- managing the active paint index

### Preview panel

Handled by [VideoPanel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/VideoPanel.java).

It is responsible for:

- karaoke preview rendering
- drawing the current and next line
- progressive fill animation across words
- background image painting

### Lyrics panel

The lyrics area is a vertical stack of Swing panels, each containing word text fields.

This is rebuilt often by calling `reload()`.

## Custom Logic

This app contains a lot of domain-specific custom behavior.

### Word timing model

Each word stores:

- `sec`
  Start position in internal timeline units
- `low_sec`
  End position in internal timeline units
- `high_sec`
  Previous or boundary reference

The app also converts these into export timings using hardcoded conversion factors in `wordLine`.

### Paint mode

Paint mode is the custom authoring mode where:

- the app starts playback
- the user marks words in sequence
- each marked word gets timing and paint state
- line endings can also be marked

This is the core feature that makes the app useful.

### Split word model

The app supports splitting a word into multiple timed pieces.

Word type enum:

- `normal`
- `splite_main`
- `splite_sub`
- `splite_sub_end`

The split system is used to:

- represent partial-word timing
- export words with `-` markers
- merge back split structures

This logic is custom and fairly complex.

### Row semantics

Words also carry line-role semantics via `Location`:

- `start`
- `middle`
- `end`

This affects:

- rendering
- export
- movement logic
- row break behavior

### Karaoke preview fill

The preview effect is not video playback.

It is a custom text fill effect:

- the preview draws outlined text
- active words receive a colored fill rectangle
- the rectangle width increases with current playback time

This is a lightweight but effective karaoke visualization.

### Mongolian-script variant

The alternate app allows each word to carry `wordM`, a Mongolian-script representation.

That variant can:

- load the same project format
- edit alternate-script values
- save `.ncp`
- export `m.nc1`

## Keyboard Shortcuts in Main App

Current main key mappings in the rebuilt jar:

- `F`
  Start or stop paint mode
- `R`
  Mark current word while in paint mode
- `E`
  Mark end-of-line break
- `T`
  Undo last painted word while in paint mode
- `Space`
  Play or pause audio
- `W`
  Reset playback position to current paint index
- `Left`
  Select previous word
- `Right`
  Select next word
- `Up`
  Move selected timing earlier
- `Down`
  Move selected timing later
- `Shift`
  Switch movement to end-line adjustment mode
- `V`
  Merge row with previous row

## File Formats

The application uses custom text formats.

### Project files

- `.ngp`
  Main karaoke project
- `.ncp`
  Project format used by the Mongolian-script variant

The project file is semicolon-separated. Each word is serialized manually into a comma-separated record.

Stored data includes:

- word index
- line index
- timing values
- word text
- split state
- paint state
- split type
- line location
- three-way split flag
- optional alternate text

### Export files

- `.ng1`
  Karaoke export
- `.nc1`
  Older or alternate karaoke export
- `cr.txt`
  Another export flavor
- `crill.txt`
  Cyrillic helper export
- `mon.txt`
  Mongolian helper export
- `-.txt`
  Plain reconstructed lyric text

These formats are built through string concatenation rather than structured serializers.

## What The App Does Well

### 1. It is workflow-oriented

The app is clearly built around the real task the user needs to complete.

It is not abstract. It provides:

- audio import
- word editing
- timing authoring
- preview
- export

That end-to-end usefulness is its biggest strength.

### 2. It is self-contained

No dependency stack, no framework complexity, no network dependency.

That makes it easy to run in controlled environments.

### 3. The `wordLine` concept is strong

Even though the implementation is overloaded, the underlying concept is right:

one word is the unit of editing, timing, export, and preview.

That gives the app a clear mental model.

### 4. The karaoke preview is effective

The preview is simple, but it gives enough feedback to judge timing visually.

### 5. The app exposes powerful manual controls

For a power user, the combination of:

- keyboard timing
- split/merge behavior
- row manipulation
- seek shifting

is very practical.

### 6. It reflects real production experience

The app contains repair tools and batch tools that only appear when software has been used against messy real-world data.

Examples:

- line order repair
- single-word line auto-fix
- batch log writing

These indicate the app evolved around production pain points.

## What The App Does Poorly

### 1. Architecture is tightly coupled

The code does not separate:

- domain logic
- UI
- persistence
- rendering
- playback

This is the single biggest maintainability problem.

### 2. `KaraokeCreatorLast` is far too large

It is effectively a god class.

It owns:

- user actions
- dialogs
- imports
- exports
- selection state
- keyboard shortcuts
- row editing
- project parsing
- batch repair tools

That makes safe change difficult.

### 3. `wordLine` is overloaded

`wordLine` should be a model object, but it also owns:

- Swing text field
- color state
- preview fill state
- rendering geometry
- serialization behavior

That mixes too many responsibilities.

### 4. File formats are brittle

The app manually builds delimited text records.

Problems:

- hard to evolve safely
- no escaping strategy
- fragile parsing
- format meaning exists only in code

### 5. Error handling is weak

Many errors are:

- printed to stdout
- swallowed
- turned into generic dialogs

The app lacks a clear failure strategy.

### 6. Windows-specific assumptions are everywhere

Examples:

- `D:\\budalt`
- `D:\\LOG.txt`
- manual backslash path construction

This harms portability and testability.

### 7. Naming quality is inconsistent

Examples:

- `wordLine`
- `myTextfield`
- `MyMeniItem`
- `oneWord`
- `readPro`
- `syncPlay1`
- `syncPlay2`

The names reflect the project’s history rather than a clean vocabulary.

### 8. Lots of commented and dead code

There is substantial legacy residue:

- disabled licensing check
- unused features
- old experiments
- duplicate export paths
- abandoned preview panel

This raises maintenance cost because it is unclear what is still relevant.

### 9. Rendering and state updates are manual

The app often rebuilds whole panels and repaints aggressively instead of applying focused updates.

This works, but it is not elegant or scalable.

## What Was Recently Improved

The original audio path was weak for long WAV files.

It had three main problems:

- it loaded large audio files too aggressively into memory
- it used `AudioInputStream.available()` incorrectly for waveform extraction
- it did heavy loading on the Swing event thread

The current source now improves that by:

- loading audio in a `SwingWorker`
- streaming waveform extraction
- caching waveform rendering
- using saner repaint timing

This was a meaningful improvement and directly addressed long-file usability.

## Dead, Legacy, or Low-Value Code

### `PaintCharPanel`

[PaintCharPanel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/PaintCharPanel.java)

This appears to be an older or unused preview component.

It is not part of the current main app path.

Value today:

- mostly historical
- maybe useful for ideas
- likely safe to remove after verification

### Disabled MAC-address licensing check

There is a commented startup restriction in [KaraokeCreator.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreator.java).

This is dead logic at the moment.

### Duplicate export methods

There are multiple export paths such as:

- `syncPlay`
- `syncPlay1`
- `syncPlay2`
- `syncMon`

Some appear redundant, partial, or legacy.

### Batch repair utilities

Methods like:

- `orderLine()`
- `oneWord()`
- `readData()`
- `readPro()`

are useful for operations staff, but they are mixed directly into the main interactive application class.

They are not useless, but they should be isolated into tools/services.

## Unclear or Risky Logic

### Timing conversion constants

The app contains hardcoded conversion factors in timing export and playback calculations.

This is risky because:

- the meaning is not documented
- precision assumptions are hidden
- different code paths use different constants

This deserves formal documentation or replacement with a clearly defined time model.

### Sticky `Shift` behavior

`Shift` is handled on key press, but not clearly reset on key release in the current shortcut handler.

That can cause confusing end-line adjustment behavior.

### Row repair and split repair mutate loaded projects

When loading projects, some code immediately normalizes split structures or row structures.

This is convenient, but it makes the load path do hidden data repair rather than pure parsing.

### Save/open coupling assumptions

The app assumes certain file neighbors by naming convention:

- project name
- text name
- `voc.wav`
- helper exports

That works operationally, but it is brittle.

## Code Quality Summary

### Runtime efficiency

- acceptable for a small desktop tool
- previously poor in the long-audio path
- still not highly optimized overall

### Maintainability

- low

### Testability

- very low

### Portability

- low

### Domain usefulness

- high

### Practical value to users

- high, if users already know the workflow

## Suggested Refactor Targets

If this app will continue to live and evolve, the highest-value refactor targets are:

### 1. Extract a real domain model

Create plain model classes for:

- song/project
- lyric line
- lyric word
- timing segment

Keep Swing components out of these classes.

### 2. Separate persistence

Move parsing and writing into dedicated classes:

- `ProjectReader`
- `ProjectWriter`
- `ExportWriter`

### 3. Separate audio services

Move playback and waveform generation into dedicated service classes rather than keeping all behavior in `WavPlayer`.

### 4. Separate preview rendering from word state

`VideoPanel` should read view-model data, not mutate domain state directly.

### 5. Replace magic constants with named timing abstractions

The app needs a clearly documented internal time unit and conversion policy.

### 6. Move batch repair tools into explicit utility modes

The production repair features should remain, but as isolated tools, not mixed into interactive editing code.

### 7. Remove dead code after confirmation

Especially:

- unused preview classes
- old export methods
- obsolete comments
- disabled experiments

## Suggested Modernization Direction

Best path:

1. Stabilize and refactor while still supporting Java 8 if required by users
2. Move to a newer LTS Java baseline later, ideally Java 21
3. Package the runtime with the application if deployment simplicity matters

The main architectural problems are not caused by Java 8. They come from code organization and accumulated legacy logic.

## Final Assessment

This is not a polished modern codebase.

It is a practical, specialized, production-shaped tool with real domain value and significant technical debt.

### In plain terms

What it is good at:

- solving a real karaoke production workflow
- giving manual control where users need it
- staying self-contained

What it is bad at:

- maintainability
- structure
- portability
- clarity of internal rules

What should be preserved:

- the workflow
- the word-based timing model
- the preview concept
- the repair tools users actually rely on

What should be changed:

- architecture
- naming
- file I/O structure
- timing model clarity
- platform assumptions

## Source Map

Main source files:

- [KaraokeCreator.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreator.java)
- [KaraokeCreatorLast.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/KaraokeCreatorLast.java)
- [MongolBichigCreator.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/main/MongolBichigCreator.java)
- [WavPlayer.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/WavPlayer.java)
- [VideoPanel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/VideoPanel.java)
- [PaintCharPanel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/source/PaintCharPanel.java)
- [wordLine.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/wordLine.java)
- [myTextfield.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/myTextfield.java)
- [MyButton.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyButton.java)
- [MyLabel.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyLabel.java)
- [MyMeniItem.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyMeniItem.java)
- [MyComfirmDialog.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyComfirmDialog.java)
- [Location.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/Location.java)
- [MyType.java](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/MyType.java)
- [admin_mn_MN.properties](/Users/kotakunp/Desktop/ngp-jar/src/karaoke/share/admin_mn_MN.properties)
