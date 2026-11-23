# Known limitations

- This workspace did not contain the referenced `MEDLENS_SPEC.md`; the original theme details and folder conventions could not be followed.
- This is an initial offline MVP. The backend proxy, live remote AI, full symptom information library, onboarding flow, and several database entities from the broader product plan remain incomplete.
- The medicine catalog includes only two names. Clinical detail fields intentionally say `Information unavailable`; the catalog needs review by a pharmacist or doctor before real-world use.
- Assistant chat history is local text storage. The flow collects a small set of follow-up answers but is not a medical triage system.
- This machine has no Android SDK and outbound DNS resolution is unavailable, so no APK has been built or installed here. GitHub Actions must run after these files are pushed to a GitHub repository before an APK artifact exists.
- The GitHub Actions workflow installs Gradle 8.9 directly and builds without relying on the Gradle wrapper or a local Gradle installation.
