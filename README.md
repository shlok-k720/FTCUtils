# FTCUtils
FTC Utils — small reusable utilities for FTC robot code (pure JVM `core` and `telemetry` + FTC-facing `ftc`).

## Core (`com.rmsftc.utils`)

- **math**
  - `MathOperand<T>` — add/subtract/multiply/divide/remainder, with Kotlin `+ - * / %` operators.
  - `MathUtils` — clamp, lerp, mapRange, angle wrap + shortest error, deadband, rate limiting, sign-preserving square/cube, epsilon-equals, positive-range normalization ([0, 360) / [0, 2π)), shortest-path angle interpolation, degree/radian conversion, `headingTo(dx, dy)`.
  - `Statistics` — mean, median, min/max, population + sample variance/stddev, percentiles (linear interpolation).
  - `Matrix` — small immutable dense matrix (add/subtract/multiply/transpose/inverse/determinant), written for this library instead of an external dependency. Implements `MathOperand` (divide is right division, remainder is element-wise), so Kotlin `+ - * / %` work on matrices.
  - **units** (`math.units`) — `NumericalValue<U>` (a value tagged with a unit; arithmetic converts operands first) and `NumericalUnit<U>` with *from-base* factors (how many of the unit make one base unit). Built-ins in `math.units.basic`: `AngleUnit` (base radian), `DistanceUnit` (base meter), `TimeUnit` (base second), `NumberUnit`.
  - **geometry** — a Pedro Pathing-compatible family. `Pose` extends Pedro's `Pose` and `Vector` extends Pedro's `Vector`, overriding every operation so results stay in these types (they *are* Pedro types, so they pass anywhere Pedro expects them). `Vector2D` wraps Pedro's (its constructor is private) with the same API plus `toPedro()`/`fromPedro()`. Pedro core is `compileOnly` here — declare it yourself, as you already do.
  - `DriveVector` — mutable drive command (forward/strafe/turn) extending Pedro's `DrivePowers`, with field-centric rotation (including an alliance-aware overload).
- **filter**
  - `Filter` — functional filter `update(newInput, covariance, oldOutput)`; factories for low-pass, high-pass, and moving average; filters combine with the `MathOperand` operations. Default `predict()`, `update(double[])` and `reset()` (no-ops) let stateful filters implement the interface.
  - `FilterState` — stateful wrapper that holds the previous output and covariance; `reset()` also resets the wrapped filter.
  - `KalmanFilter` — linear Kalman filter over a matrix state (F/B/H/Q/R), Joseph-form covariance update. Implements `Filter` (scalar update is a 1-D predict+correct convenience).
  - `ExtendedKalmanFilter` — EKF for nonlinear models: caller supplies the process/measurement functions and their Jacobians. Implements `Filter` the same way.
- **control**
  - `Controller` — functional controller `evaluate(setpoint, input)`; P/I/D/PID/PIDF factories, piecewise composition, `MathOperand` combinators.
  - `PiecewiseController` — switches controllers by |error| threshold.
  - `Limiter` — rate + output limiter driven by a `LimiterConfig`.
  - `LimiterConfig`, `PidConfig` — tunable configs (see below).
- **config**
  - `Configuration<T>` / `IConfiguration<T>` / `Configurator<T>` — config framework: `defaults()` then a configurator lambda overrides.
  - `config.vars` — `Val<T>` (read-only value or live view), `Var<T>` (read/write value or provider alias; named vars carry a `Section`, register in `Variables`, support `Validator`s, mutability locking, and `at()` temporary overrides), `ValGetter`, `ValSetter`, `VarProvider`, `Section`, `Validation`, `Validator`, `Modifier`, `Variables`, `GarbageCollector`, and Kotlin aliases/factories in `config.vars.kt` (`DoubleVar`, `IntVar`, `StrVar`, `BoolVar`).
- **blackboard** — `Blackboard` + `BlackboardCallback`: a shared key-value store (string or enum keys) with change callbacks, for passing state between behavior trees, state machines, and commands. API-compatible with BehaviorArchitectures' `util` Blackboard (see the plan below).
- **logger** — `Logger` (static log-event bus: `log(caption, data)`, `attachLogger`/`removeLogger`/`reset`) + `LogSystem` (the sink interface). API-compatible with BehaviorArchitectures' `util` Logger (minus its caption constants).
- `Enums` — `AllianceColor` (RED/BLUE/UNKNOWN, with `fieldCentricOffset()`), `RobotMode` (AUTONOMOUS/TELEOP/TEST with time limits), `MatchMode` (PLAYOFFS/QUALIFICATIONS).

## Telemetry (`com.rmsftc.utils.telemetry`)

Pure JVM telemetry utilities, wired into `ftc` via `api(project(":telemetry"))`.

- `Logger` — the output sink interface (`log(String)`), with `SystemLogger` (stdout), `StringLogger` (collects in memory), and `MultipleLogger` (fan-out).
- **selection** — a question/answer menu system. `Question` + `Choices` + `AnswerChoice` render to text through a `DisplayFormat` (`DisplayFormat.instance` for plain text, `DisplayFormat.HTML` for dashboard HTML); `EnumChoices` builds choices from an enum; `Select` has short factories (`a`/`q`/`c`/`p`). `Prompter` runs a queue of questions: `getString()` renders the current one, `highlight(i)` moves the selection, `select(i)`/`confirm()` answer programmatically and advance, `getAnswers()` collects the picks. The gamepad-driven version lives in `ftc` (`GamepadMenu`).
- **html** — `Html`, a tiny HTML string builder (escape, bold/italic, `<br/>`, `<hr/>`, lists), and `HtmlLogger` (an `AdvancedLogger` that accumulates HTML: plain messages escaped, `logHTML` markup passed through).

## Ftc (`com.rmsftc.utils.ftc`)

FTC-facing code:

- **daemons** — `DaemonManager` (Sinister-scanned `@FTCDaemon` registration; daemon methods must be static, zero-arg, void) and `ClockManager`, the classic daemon usage: one static `Clock` (get/set, defaults to `Clock.SYSTEM`) whose daemon snapshots time and computes `dt` every loop (`getTime()`, `getDt()`, `getDtSeconds()`).
- **utils** — `Clock` (any time supplier as `NumericalValue<TimeUnit>`, nanoseconds by default; `nowNanos()`/`asLongSupplier()` for plain longs; `Clock.Helper` tracks dt per instance), `Storage` (SharedPreferences-backed persistence with typed getters and a generic `Value<T>` format/parse interface — built-ins for String/Int/Long/Double/Float/Boolean, custom implementations for anything else), `Hooks` (event-loop lifecycle wiring for the opmode manager + storage).
- **hardware** — `SmartMotor` (power target + rate limiter, applied in `update(dtSeconds)`) and `MecanumDrive` (four-`SmartMotor` mecanum drive: robot-centric `drive` — including a `drive(DriveVector)` overload — + field-centric `driveFieldCentric`, wheel powers normalized, applied in `update(dtSeconds)`).
- **telemetry** — `TelemetryLogger` (a telemetry-module `AdvancedLogger` backed by an FTC `Telemetry`; pass `FtcDashboard.getInstance().getTelemetry()` for dashboard HTML) and `GamepadMenu` (dpad to move, `a` to confirm, over a `Prompter`).

## BehaviorArchitectures plan

FTCUtils is intended to eventually replace BehaviorArchitectures' `util` module: BA's modules would depend on FTCUtils (`api(...)`) and BA's `util` would be deleted. The pieces are already in place:

- BA `com.rmsftc.util.blackboard.Blackboard`/`BlackboardCallback` ↔ FTCUtils `com.rmsftc.utils.blackboard.*` — same API.
- BA `com.rmsftc.util.logger.Logger`/`LogSystem` ↔ FTCUtils `com.rmsftc.utils.logger.*` — same mechanism and API; BA's caption constants stay BA-side (they name BA packages).

The later swap is therefore: change the dependency in BA's four modules, rename imports `com.rmsftc.util` → `com.rmsftc.utils`, move the caption constants into BA, delete `util`. No behavior changes expected; BA's util tests should pass against the FTCUtils equivalents.
