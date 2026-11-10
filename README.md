# FTCUtils
FTC Utils — small reusable utilities for FTC robot code (pure JVM `core` + FTC-facing `ftc`).

## Core (`com.rmsftc.utils`)

- **math**
  - `MathOperand<T>` — add/subtract/multiply/divide/remainder, with Kotlin `+ - * / %` operators.
  - `MathUtils` — clamp, lerp, mapRange, angle wrap + shortest error, deadband, rate limiting, sign-preserving square/cube, epsilon-equals.
  - `Matrix` — small immutable dense matrix (add/subtract/multiply/transpose/inverse/determinant), written for this library instead of an external dependency. Implements `MathOperand` (divide is right division, remainder is element-wise), so Kotlin `+ - * / %` work on matrices.
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

## Ftc (`com.rmsftc.ftcImpl`)

- `DashboardVars` — uploads named config vars and modifiers to the FTC Dashboard (`upload`, `registerAllToFtcDash`); dashboard edits call `Var.set`, so validation and locking still apply. Modifier entries are display-only.
