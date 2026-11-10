# FTCUtils
FTC Utils — small reusable utilities for FTC robot code (pure JVM `core` + FTC-facing `ftc`).

## Core (`com.rmsftc.utils`)

- **math**
  - `MathOperand<T>` — add/subtract/multiply/divide/remainder, with Kotlin `+ - * / %` operators.
  - `MathUtils` — clamp, lerp, mapRange, angle wrap + shortest error, deadband, rate limiting, sign-preserving square/cube, epsilon-equals.
- **filter**
  - `Filter` — functional filter `update(newInput, covariance, oldOutput)`; factories for low-pass, high-pass, and moving average; filters combine with the `MathOperand` operations.
  - `FilterState` — stateful wrapper that holds the previous output and covariance.
- **control**
  - `Controller` — functional controller `evaluate(setpoint, input)`; P/I/D/PID/PIDF factories, piecewise composition, `MathOperand` combinators.
  - `PiecewiseController` — switches controllers by |error| threshold.
  - `Limiter` — rate + output limiter driven by a `LimiterConfig`.
  - `LimiterConfig`, `PidConfig` — tunable configs (see below).
- **config**
  - `Configuration<T>` / `IConfiguration<T>` / `Configurator<T>` — config framework: `defaults()` then a configurator lambda overrides.
  - `config.vars` — `Val<T>` (read-only value or live view), `Var<T>` (read/write value or provider alias), `ValGetter`, `ValSetter`, `VarProvider`.
