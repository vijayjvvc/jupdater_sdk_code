# Changelog

All notable changes to this project will be documented in this file. The format is based on [Keep a Changelog](https://keepachangelog.com).

## [2.3.1] - 2026-10-09

### Added
- **Maven / JitPack Support:** The SDK can now be imported directly via JitPack Maven repository (`com.github.vijayjvvc:jupdater_sdk_code:2.3.1`).

### Changed
- **Privacy:** Android ID collection has been completely removed (`android_id` parameter removed). Managed mode still sends device/app data used for dashboard insights; custom-server mode sends only `pid` and `version_code`.
- **Privacy:** the installation `uid` is now a random UUID (existing installs keep their stored ID).
- Added `PRIVACY.md` documenting every field and the integrating developer's GDPR / ePrivacy responsibilities. Removed the "no data collection" claims from the docs.
- **Logger:** new `JLogLevel` (`NONE`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `VERBOSE`) via `JUpdaterConfig.setLogLevel(...)`. Default is `WARN` (previously info logs were always on). Payload is logged only at `VERBOSE`; the API key is never logged. `setDebug(boolean)` is deprecated.
- Performance: one shared on-demand worker thread instead of a new thread per check, in-flight guard, configurable check interval (`setMinCheckIntervalMinutes`, default 5) with answers served from the cached server state, single PackageManager call, lifecycle tracking without timers/executors, 10 s timeouts applied before connecting, bounded response size, lazy log message building.

### Fixed
- Build failed without `local.properties` (double-quoted BuildConfig values).
- Offline fallback opened the literal URL `"backend"` in managed mode; the last server URL is now cached.
- A null value in the payload (e.g. missing field) no longer aborts the whole check; JSON is built with `JSONObject`.
- `force_update_gap` of `0` no longer throws; it is treated as `1`.
- No more static `Activity` Context retention.
- `UpdateBlocking` is no longer exported; null / unlaunchable URLs no longer crash.
- Added `INTERNET` permission to the library manifest.
- Server `update_url` must be `http(s)`.

### Removed
- Internal helper classes that were accidentally public (`JThread`, `JDateFormat`, `UpdateChecker`/`Constants` internals).

## - 2026-09-25

### Added
- Updated architecture support for both Managed Cloud and Self-Hosted server environments.
- Added real endpoint URL for Managed Server (`https://jupdater.qtlws.com/api/check-update`).
- Integrated Ventures Hub platform credits and services (an independent software company by QTLWS providing managed infrastructure and up to 3 free app configurations for developers).


## - 2026-06-15

### Added
- Added architecture support for both Managed Cloud and Self-Hosted server environments.
- Added parameter validation forcing `FallbackUrl` / Custom URL for self-hosted instances and API Keys for Managed Servers.

### Fixed
- Resolved minor internal bugs to improve SDK stability.

---

## - 2026-05-20

### Added
- Added an open-link helper utility to streamline external URL redirections.

### Fixed
- Fixed minor edge-case bugs.

---

## - 2026-04-10

### Changed
- Configured Server URL as a mandatory initialization parameter.
- Restructured threshold configurations to reject values lower than 1.

### Fixed
- Fixed minor internal initialization bugs.

---

## - 2026-03-01

### Added
- Initial public release of the JUpdater SDK.
- Core Android update-checking engine.
- Native support for Force Update and Optional Update user flows.
- Version-code-based update detection algorithm.
- Direct APK download and installation management.
- Debug logging framework for easier implementation verification.
- Flexible custom server response parsing engine.

### Compatibility
- Native Android applications (XML-based layouts).
- Kotlin-based Android projects.
- Java-based Android projects.

---

## [Unreleased]

### Planned Features
- Comprehensive download and update analytics dashboard.
- Advanced phased-release and rollout management.
- Intelligent, network-aware update strategies.
- Extended framework compatibility (Jetpack Compose, Flutter, React Native).
