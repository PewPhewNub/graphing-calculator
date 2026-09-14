# Graphing Calculator

![Application Window](images/Main_Window.png)

A desktop graphing calculator built with Java and JavaFX.
The application supports multiple plot types, saving/loading, image export, and interactive graph exploration.

## Documentation

📖 **[How to Use](docs/HOW_TO_USE.md)**

Learn how to create and configure plots, enter mathematical expressions, use ODE Mode, navigate the graph, and use the application's controls.

### What's New in v1.1

### 🧮 ODE Mode

The biggest addition in v1.1 is **ODE Mode**, bringing numerical differential-equation solving directly into the graphing calculator.

ODE Mode supports:

* Numerical solution of first-order ODEs using **fourth-order Runge–Kutta (RK4)**
* User-defined initial conditions
* A **movable initial point** directly on the graph
* Configurable numerical step size
* Automatic generation of additional solution segments as the viewport is explored
* Optional slope fields
* Undo/redo support for initial-point changes
* Saving and loading ODE plots

This is accompanied by a broader refactor of the plotting and computation systems, allowing each plot type to manage its own computation more independently.

### Other Major Additions

* Added variable and parameter support
* Added per-plot computation engines and parallelization
* Added mouse-centered zooming
* Added improved axis rescaling
* Improved project saving/loading
* Expanded user and technical documentation

## Features

- Function plots
- Parametric plots
- Polar plots
- Implicit plots
- Expression parsing
- Zoom and pan
- Curve inspection
- Undo / redo
- Save and open projects
- Image export

## Screenshots

### Exported Graph

![Exported Implicit Plots](images/Implicit_Plot_Showcase.png)

Graphs can be exported as PNG images with optional transparent backgrounds.

### Plot Editor

![Plot Editor](images/Plot_Editor_Showcase.png)

The editor allows creation and configuration of multiple types within a single project.

## Implementation

The application is built from scratch using Java and JavaFX.

Major components include:
- Custom mathematical lexer and parser written from scratch
- Plot model system
- Rendering pipeline for curves, axes, grids, and overlays
- Adaptive curve rendering
- Camera/viewport system with zooming and panning
- Command-based Undo/Redo system
- JSON-based project saving and loading using Jackson

### Expression Parser

The application uses a custom mathematical expression parser built from scratch.

The parser pipeline consists of:

- Lexing: converts text expressions into tokens
- Parsing: converts tokens into an expression tree
- Evaluation: computes values during plotting

The parser uses a recursive descent approach to build an abstract syntax tree.

The parser supports:
- Arithmetic operations
- Variables
- Functions
- Operator precedence
- Parentheses

## Project Structure

```text
src/
├── app             Application entry point
├── interaction     Input handling and undo/redo
├── math            Mathematical utilities
├── parser          Expression parsing
├── persistence     Project saving/loading
├── plotting        Plot models and curve generation
├── rendering       Canvas rendering pipeline
├── scene           Scene coordination
├── settings        Configuration
└── ui              JavaFX interface
```

## Running

### Requirements

#### Windows

The packaged release is currently available for **Windows only**.

* Java 21 or newer
* JavaFX 25
* Jackson 2.9.9

### From Release

The latest packaged release is currently available for Windows.

1. Download the latest release from the [Releases](https://github.com/PewPhewNub/graphing-calculator/releases) page.
2. Extract the downloaded archive.
3. Run `run.bat`.

A Linux release is planned for a future release.

### From Source

The project can also be cloned and run directly from source.

> **Note:** The project currently does not use Maven or Gradle. Dependencies therefore need to be configured manually. A Maven-based build system may be added in a future update.

#### 1. Clone the Repository

Clone the repository using Git:

```bash
git clone https://github.com/PewPhewNub/graphing-calculator.git
cd graphing-calculator
```

Alternatively, the repository can be downloaded as a ZIP file from GitHub and extracted manually.

#### 2. Install Java

Install **Java 21 or newer** and make sure your Java installation is correctly configured.

The project was developed and tested using a newer Java runtime, but Java 21+ is the intended minimum version.

#### 3. Install JavaFX

The project uses **JavaFX 25**.

Download the JavaFX SDK and extract it somewhere on your system.

The JavaFX SDK contains a `lib` directory containing the required JavaFX JAR files.

Add the JAR files from the JavaFX `lib` directory to the project's **Referenced Libraries** in VS Code, or configure them as libraries in your IDE.

The JavaFX libraries must also be supplied to the JVM using the Java module path.

For example:

```text
--module-path "PATH_TO_JAVAFX/lib"
--add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.web
```

If running directly from VS Code, these can be added to the Java launch configuration through the appropriate VM arguments.

#### 4. Install Jackson

The project uses **Jackson 2.9.9** for JSON-based project saving and loading.

The required Jackson JAR files must be downloaded and added to the project's **Referenced Libraries**, alongside the JavaFX libraries.

Jackson is used by the persistence system to serialize and deserialize calculator projects.

#### 5. Configure VM Arguments

When launching the application, JavaFX requires the JavaFX module path and modules to be specified.

A typical launch configuration should contain VM arguments similar to:

```text
--module-path "PATH_TO_JAVAFX/lib"
--add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.web
```

Replace `PATH_TO_JAVAFX/lib` with the location of the JavaFX SDK's `lib` directory on your system.

The exact launch configuration may vary depending on the IDE and operating system.

#### 6. Run the Application

Once JavaFX and Jackson have been added to the project, open the project in VS Code or another Java IDE.

Run:

```text
src/app/Main.java
```

or the `app.Main` class.

If the dependencies and JavaFX VM arguments are configured correctly, the application should launch normally.

### Project Structure

The main source code is located under `src/` and is organized into separate systems for parsing, mathematics, plotting, rendering, interaction, persistence, and the user interface.

For more information about how the calculator works internally, see the technical documentation.

## Usage

1. Create a new plot from the plot editor.
2. Select the desired plot type.
3. Enter an expression or configure the required plot parameters.
4. Generate the plot.
5. Use the mouse to pan and zoom around the graph.
6. Use the available plot controls to modify or inspect plots.
7. Save projects using the file menu.
8. Export graphs as images when required.

For a detailed guide to the calculator's features and controls, see [`docs/HOW_TO_USE.md`](docs/HOW_TO_USE.md).

## Future Improvements

Possible future improvements include:

* Maven/Gradle-based dependency management and project setup
* Linux packaging
* Additional plotting features
* Performance improvements for complex implicit plots
* More customization options
* Improved project management
* Additional documentation

## License

This project is licensed under the MIT License.

See the [LICENSE](LICENSE) file for details.
