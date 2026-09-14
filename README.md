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
Requirements

The packaged release is currently available for Windows only.

Java 25 or newer
Maven 3.9+

JavaFX and Jackson dependencies are managed automatically by Maven.

### From Release

The latest packaged release is currently available for Windows.

Download the latest release from the Releases page.
Extract the downloaded archive.
Run run.bat.

A Linux release is planned for a future release.

### From Source

The project uses Maven for dependency management and building.

Maven automatically downloads the required JavaFX and Jackson dependencies, so they do not need to be downloaded or added manually.

#### 1. Clone the Repository

Clone the repository using Git:

git clone https://github.com/PewPhewNub/graphing-calculator.git
cd graphing-calculator

Alternatively, the repository can be downloaded as a ZIP file from GitHub and extracted manually.

#### 2. Install Java

Install Java 25 or newer and make sure your Java installation is correctly configured.

Check your Java installation with:

java -version
javac -version

Both should report Java 25 or newer.

#### 3. Install Maven

Install Maven 3.9 or newer.

Verify that Maven is available:

mvn -version

Maven should report the Java installation being used by the build.

#### 4. Build the Project

From the project directory, run:

mvn clean package

Maven will automatically:

Download the required dependencies
Compile the source code
Copy application resources
Package the application into a JAR
Generate the runtime dependency classpath

The resulting build files are placed in the target/ directory.

To compile without packaging, use:

mvn clean compile

#### 5. Run the Application

The main application class is:

app.Main

The project can be launched from an IDE configured with the Maven project, or using an appropriate JavaFX runtime configuration for the target platform.

The Maven project configuration is contained in pom.xml.

## Project Structure

The main source code is located under src/ and is organized into separate systems for parsing, mathematics, plotting, rendering, interaction, persistence, and the user interface.

Maven configuration and dependency management are defined in pom.xml.

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
