# Syfo diagnose code generator json
Applikasjon for å generere opp json-filer som benyttes i 
syfosm-common-biblioteket (https://github.com/navikt/syfosm-common)

> [!WARNING]
> Dette repoet er deprekert og arkivert. Bruk [navikt/diagnosekoder](https://github.com/navikt/diagnosekoder)


### Oppdatering av diagnosekoder
Diagnosekoder, både ICD10 og ICPC2, får vi fra ehelse. Det kommer normalt ny versjon av diagnosekodene hvert år.
Url som må oppdateres i koden ser slik ut:
```
val icd10Url = URL("flotticd10url").openConnection() as HttpURLConnection
val icpc2Url = URL("flotticpc2Url").openConnection() as HttpURLConnection
```

### Bygging av applikasjon
Kjør Bootstrap.kt main-metoden, og hent ut json-filene med kodeverkene, 
og legg dei til manuelt i syfosm-commons-biblioteket(https://github.com/navikt/syfosm-common)

Verifiser at filene er gyldig json og rett eventuelle feil. 

for å kjøre main metoden fra terminal kjør følgende kommando:
``` bash
./gradlew run
```



## Oppgradering av gradle wrapper
Finn nyeste versjon av gradle her: https://gradle.org/releases/

``` bash
./gradlew wrapper --gradle-version $gradleVersjon
```

## Henvendelser
Dette prosjeket er vedlikeholdt av [navikt/teamsykmelding](CODEOWNERS)

Spørsmål knyttet til koden eller prosjektet kan stilles som
[issues](https://github.com/navikt/syfodiagnosecodegeneratorjson/issues) her på GitHub

### For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#team-sykmelding](https://nav-it.slack.com/archives/CMA3XV997)
