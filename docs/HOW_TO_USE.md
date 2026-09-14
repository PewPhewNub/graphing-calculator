# Graphing Calculator — User Guide

## 1. Introduction

Welcome to the Graphing Calculator.

This application is a JavaFX-based mathematical graphing program designed to provide an interactive environment for plotting and exploring mathematical expressions.

The calculator supports multiple types of graphs, interactive navigation, variables, numerical differential-equation plotting, undo/redo functionality, and persistent graph configurations.

This guide explains how to use the application, how to enter mathematical expressions, how each plotting mode works, and how to interact with graphs.

---

# 2. Getting Started

When the application starts, you are presented with the graphing workspace.

The main graph displays:

* The coordinate axes
* Grid lines
* Plotted functions and equations
* Interactive graph controls

Depending on the selected mode, the application provides different controls for entering and manipulating mathematical expressions.

The basic workflow is:

1. Select the appropriate plotting mode.
2. Enter your mathematical expression.
3. Create/add the plot.
4. Interact with the resulting graph.
5. Modify the expression or parameters as required.

---

# 3. Expression Syntax

The calculator uses mathematical expressions to describe graphs.

## 3.1 Numbers

Numbers can be entered directly.

Examples:

```text
5
3.14
-2
0.001
```

Decimal values are supported.

---

## 3.2 Basic Arithmetic

The standard arithmetic operators can be used.

| Operation      | Operator | Example |
| -------------- | -------- | ------- |
| Addition       | `+`      | `x + 2` |
| Subtraction    | `-`      | `x - 2` |
| Multiplication | `*`      | `2*x`   |
| Division       | `/`      | `x/2`   |
| Power          | `^`      | `x^2`   |

Implicit multiplication (2x being equivalent to 2*x) is supported, but may now always work.
For clarity, multiplication should be written explicitly where required by the expression parser.

Examples:

```text
2*x
x/3
x^2
3*x^2 + 2*x - 1
```

---

# 4. Parentheses

Parentheses can be used to control the order of operations.

Examples:

```text
(x + 2)^2
(x - 1)/(x + 3)
2*(x + 1)
sin(x + 1)
```

Parentheses are particularly important when using functions.

---

# 5. Mathematical Functions

Functions are written using their function name followed by parentheses containing their argument.

For example:

```text
sin(x)
cos(x)
tan(x)
```

### Important

Functions require parentheses.

Correct:

```text
sin(x)
cos(x)
sqrt(x)
```

Incorrect:

```text
sin x
cos x
sqrt x
```

The expression parser treats function calls differently from ordinary variables, so the argument must be explicitly provided using parentheses.

---

# 6. Trigonometric Functions

Trigonometric functions can be used inside expressions.

Examples:

```text
sin(x)
cos(x)
tan(x)
```

They can also be combined with other expressions:

```text
2*sin(x)
sin(x)^2
sin(2*x)
x*cos(x)
```

Functions can be nested inside other expressions where supported.

For example:

```text
sin(x^2)
cos(2*x + 1)
```

---

# 7. Function Composition

Functions can be used as part of larger mathematical expressions.

Examples:

```text
sin(x) + cos(x)
sin(x)*cos(x)
sqrt(x^2 + 1)
```

Parentheses can be used to make the intended order of evaluation explicit.

---

# 8. Cartesian Plotting

Cartesian plotting is used for ordinary functions of the form:

```text
y = f(x)
```

For example:

```text
y = x^2
```

produces a parabola.

Other examples include:

```text
y = x^3
y = sin(x)
y = cos(x)
y = x^2 + 2*x - 3
```

Cartesian plots are useful for exploring ordinary functions and their behavior over the coordinate plane.

---

# 9. Multiple Plots

Multiple plots can be displayed simultaneously.

For example, you can compare:

```text
y = x^2
```

with:

```text
y = 2*x + 1
```

This allows different mathematical functions to be viewed together.

Each plot maintains its own computational state, allowing plots to be independently manipulated and evaluated.

---

# 10. Variables and Parameters

The calculator supports variables and parameters within its plotting/computation system.

Variables allow mathematical expressions to depend on values other than the primary graph variable.

For example, an expression can contain a parameter whose value can be changed to observe how the resulting graph changes.

This can be useful for exploring families of functions.

For example, conceptually:

```text
y = a*x^2
```

can represent a family of parabolas whose shape depends on `a`.

When adding a variable to a function definition, you must also add a variable component to the list, it will NOT be added automatically. After creating the component, you must also rename the variable name to the corresponding name of the variable present in the definition.

---

# 11. Parametric Graphs

Parametric equations describe both coordinates using a parameter.

A parametric curve can be represented using:

```text
x = x(t)
y = y(t)
```

where `t` is the parameter.

A common example is a circle:

```text
x(t) = cos(t)
y(t) = sin(t)
```

Parametric equations are useful when a curve cannot conveniently be represented as a single-valued function `y = f(x)`.

They can also represent curves that loop, self-intersect, or have multiple values of `y` for the same `x`.

---

# 12. Polar Graphs

Polar equations represent a curve using an angle and a radial distance.

A polar equation is written in terms of:

```text
r
```

and the angular variable (\u03B8). Type out \'theta\' (fully in lowercase) and the character will be replaced by the symbol. Polar functions will NOT work without the symbol as the independent variable.

For example, a circle-like or cardioid-style curve can be represented using expressions involving trigonometric functions.

Polar plotting is particularly useful for studying:

* Circles
* Spirals
* Rose curves
* Cardioids
* Other rotationally symmetric curves

---

# 13. Implicit Graphs

Implicit equations define relationships between `x` and `y` without explicitly solving for `y`.

For example:

```text
x^2 + y^2 = 25
```

represents a circle.

Implicit plotting is useful for equations such as:

```text
x^2 + y^2 = 25
```

and other curves that may be difficult or impossible to express conveniently as:

```text
y = f(x)
```

Implicit graphs are experimental and are not guaranteed to wrok reliably.

---

# 14. ODE Mode

The application includes a dedicated mode for plotting ordinary differential equations.

An ordinary differential equation describes a relationship involving a function and one or more of its derivatives.

ODE plotting differs from ordinary function plotting because the graph is obtained numerically from the differential equation and an initial condition.

---

# 15. Initial Conditions

An initial condition specifies the starting point from which an ODE solution is calculated.

The initial point can be represented by its coordinates.

The application displays the initial point on the graph when it is selected.

This allows the starting condition of the numerical solution to be inspected visually.

---

# 16. Moving the ODE Initial Point

The ODE initial point can be moved interactively.

To move the initial point:

1. Select the ODE plot.
2. Locate the initial-condition point.
3. Drag the point to a new location.
4. The ODE solution is recalculated using the new initial condition.

This provides an interactive way of exploring how changing the initial condition affects the solution.

---

# 17. Undoing Initial-Point Changes

Changes to the ODE initial point participate in the application's undo system.

After moving the initial point, the change can be undone using the normal undo functionality.

This makes it possible to experiment with different initial conditions without permanently losing the previous state.

---

# 18. ODE Step Size

Numerical ODE solutions are calculated using discrete steps.

The step size controls how far the numerical solver advances during each calculation step.

In general:

* Smaller step sizes can provide greater numerical resolution.
* Larger step sizes can reduce computation time.
* Extremely large steps may reduce the quality of the numerical approximation.

When experimenting with an ODE, adjust the step size depending on the behavior of the equation and the desired balance between accuracy and computation.

---

# 19. Navigating the Graph

The graph is interactive and can be explored using mouse controls.

You can zoom into regions of interest and move around the coordinate plane.

Interactive navigation is particularly useful when studying:

* Local behavior
* Intersections
* Rapidly changing functions
* ODE solutions
* Large-scale graph behavior

---

# 20. Mouse-Centered Zooming

Zooming is centered around the mouse position.

This means that when you zoom in on a particular region, the point underneath the cursor remains the focus of the zoom.

This makes it easier to explore specific parts of a graph without repeatedly repositioning the view.

---

# 21. Axis Rescaling

The graph supports axis-specific rescaling.

To rescale an axis, use the appropriate modifier key together with the scroll wheel.
