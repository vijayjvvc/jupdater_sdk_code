# JUpdater – Privacy, GDPR & ePrivacy notes for integrating developers

> This document describes what the SDK does. It is not legal advice.

> 🔒 **Privacy Note (v2.3.1+):** Android ID collection has been **completely removed**. The SDK does not read or collect Android ID, hardware serial numbers, advertising IDs, IMEI, or MAC addresses.

## What is sent, and when

| Mode | Request | Data sent |
|------|---------|-----------|
| **Custom server** (`useCustomServer`) | `GET` to *your* server | Only `pid` (package name) and `version_code` as query parameters. No device data, no identifier. |
| **Managed server** (`useManagedServer`) | `POST` to the managed server (`https://jupdater.qtlws.com/api/check-update`) | The fields below, used to build the dashboard insights. |

A check runs when the app comes to the foreground (at most once per check interval, default 5 minutes; configurable with `setMinCheckIntervalMinutes`).

### Managed mode payload

| Field | Content |
|-------|---------|
| `uid` | Random UUID generated on first run and stored in the app's private preferences. Not derived from hardware. Reset on uninstall / clear data. |
| `apiKey` | Your JUpdater API key (also sent as `x-api-key` header). |
| `build_number` | JUpdater SDK version |
| `brand`, `manufacturer`, `model`, `device`, `mainboard` | Device make / model / board (`android.os.Build`) |
| `os_version`, `sdk` | Android version and API level |
| `country` | Country of the device's locale setting (not geolocation) |
| `package_name`, `app_version_code`, `app_version_name` | Your app's identity and version |
| `firstInstallTime` | When the app was first installed on the device |

**Not collected:** Android ID, advertising ID, IMEI, MAC address, location, contacts or any user content. (The Android ID was removed in v2.3.1.)

The server also sees the device's IP address as part of the HTTPS connection.

## Your responsibilities as the integrating developer

You decide whether and when the SDK starts (`JUpdater.getInstance().init(...)`), so you are the data controller for this processing in your app. In particular:

1. **Disclose it in your privacy policy.** Name the update-check service, the categories above, the purpose (update checks and aggregated update/usage insights) and the recipient (Ventures Hub / QTLWS as the managed-server operator). Mention the retention period and hosting location published by the operator.
2. **Have a legal basis (GDPR Art. 6).** The random `uid` and the device data together are pseudonymous personal data. The update check itself may rest on legitimate interest or contract; the dashboard insights are analytics and should be assessed separately.
3. **ePrivacy (Art. 5(3)) / national laws (e.g. TTDSG §25 in Germany).** Storing the `uid` on the device and reading device information for insights is generally *not* "strictly necessary" for showing an update prompt. If you serve users in the EU/EEA, plan to obtain consent **before** calling `init()` in managed mode, and call `init()` only after consent has been given.
4. **Processor agreement (GDPR Art. 28).** If you use the managed server, conclude a data-processing agreement with its operator.
5. **Data subject rights.** Be able to handle access / deletion requests that involve the `uid` (the user can reset it by clearing the app data).
6. **Custom-server mode** sends no device data, but your own server still receives the user's IP address and the app version; cover that in your own policy.

## Suggested privacy-policy wording (adapt it)

> To tell you when a new version of the app is available, we use the JUpdater update service. When the app is opened, it sends the app's package name and version, a randomly generated installation ID, the device brand, model, Android version, device locale country and the app's first-install time to the JUpdater server operated by Ventures Hub (QTLWS). We use this data to provide update prompts and aggregated statistics about app versions in use. Legal basis: [your basis]. Retention: [period].

## Logging

The SDK never logs the API key. The request payload is only logged at `JLogLevel.VERBOSE`; use `JLogLevel.WARN` (default) or lower in production builds.
