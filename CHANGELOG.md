# Changelog

All notable changes to this project will be documented in this file. The format is based on [Keep a Changelog](https://keepachangelog.com).

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
