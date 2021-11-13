[![Java CI with Maven](https://github.com/kristiania-pgr203-2021/pgr203-exam-ssovesen/actions/workflows/maven.yml/badge.svg)](https://github.com/kristiania-pgr203-2021/pgr203-exam-ssovesen/actions/workflows/maven.yml)

### README.md skal inneholde:

* [ ] Korrekt link til Github Actions
* [ ] Beskrivelse av prosjektets funksjonalitet, hvordan vi har bygget det
* [ ] Beskrivelse av hvordan man kjører/skal teste programmet
* [ ] Beskrivelse eventuell ekstra leveranse utover minimum
* [ ] Et diagram som viser datamodellen
* [ ] Hvordan man bygger, konfigurerer og kjører løsningen
* [ ] Dokumentasjon av design på løsningen
* [ ] Beskrivelse av erfaringene med arbeidet og løsningen

### FUNKSJONALITET:

Programmet vårt gir brukeren mye funksjonalitet og kontroll over å lage et spørreskjema. Brukeren kan godt komme i gang med a lagre et spørreskjema. Da kan de bestemme hvor mange kategorier spørreskjema skal ha og kan tilknytte flere spørsmål under hvert kategori. Ved å opprette et spørsmål har brukeren flere muligheter med tank på hvordan den skal besvares. Bruker setter in spørsmål og bestemmer seg en skala med. For eksempel man kan ha en enkelt besvarelse av «Ja» eller «Nei» med skala på 2 eller en større skala med merkelapper «Veldig Enig» eller «Helt Uenig» og en skala på 10. Under hele prosessen har brukeren også mulighet til å redigere eller slette et spørsmål.

### KJØR PROGRAMMET
Når et spørreskjema er bygget og lagret med alle kategoriene og spørsmålene brukeren ønsker, kan den vises og fylles ut på Show Questionnaire siden. Her kan man registrere seg og fylle ut en eller flere spørreskjemaer. Når den er fullført registreres besvarelser i databasen sammen med en ID som er unik til brukeren. Hvis man velger å fylle ut flere spørreskjemaer skal browseren huske hvem man er. Deretter kan brukeren se Show Answers siden for å se gjennomsnitt av besvarelse for hvert spørsmål.

### ERFARINGEN VÅR
Vi har jobbet veldig hardt med dette prosjektet og er veldig stolte over hvor vi har havnet. Siden vi har jobbet sammen på tidligere arbeidskrav har vi skapt veldig god teamarbeid, tillit, og kommunikasjon som har vært svært viktig under eksamen. Vi begynte å jobbe før github-repoet ble publisert ved å lese nøye gjennom oppgaveteksten og diskutere hvordan programmet skulle fungere. Vi også begynte å tegne UML-diagram over hvordan databasen skulle se ut. Da hadde vi et god utgangspunkt for å begynne å kode. Vi par-programmert med TDD for å opprette nesten hele prosjektet. Når vi hadde den grunnleggende funksjonalitet, begynte vi å dele arbeid i vår egne prosjekter, som å lage ny funksjonalitet eller refactoring av koden.

### DIAGRAM AV PROGRAMMET
* [ ] Insert png UML diagram with explanation of relationship between tables
* [ ] Insert png Diagram of program and how it connects

### EXTRAOPPGAVER VI HAR KLART
- Vi har avansert datamoddell med 5 tabeller
- Vi har avansert funksjonalitet ved at hvert svaralternativet besvares på en skala 
- Brukeren bestemmer skalaen og merkelappen på skalaen vet bruk av high_value og low_value
- Et sett med spørsmål kan knyttes sammen til en kategori som knyttes til en spørreundersøkelse
- Når brukeren besvarer en spørreundersøkelse registreres fornavn, etternavn og epost
- Implementert Cookies i showQuestionnaire.html sånn at browseren husker hvem det er som svarer på Questionnaires. Dette også lagres i databasen med en Person Id. Dette kan eventuelt brukes til å vise alle besvarelse av en person.
- Vi bruker abstraksjon i både DAO og Controller-klassene. Vi har en AbstractDao klasse som er brukt av alle Dao-klassene og en HttpController med handle() metode som må brukes av alle Controller-klassene.
- Vi har brukt HTTP response 301 for å sende brukeren tilbake til forrige side etter de har utført en POST 
- Vi har håndtert riktig encoding hvis brukeren skriver på norsk med bruk av URLDecorder i HttpReader når action=POST blir fulført og at hver html-side har <html lang="no"> da input blir håndtert riktig.
- I tilfelle brukeren setter in feil port eller request target "/" får men likevel innholdet av index.html istedenfor 404
- Vi har rammeverk rundt http-håndtering med HttpReader, HttpClient, HttpPostClient
- Vi har laget diagram av hvordan programmet virker samt UML diagram av database tabellene
- Vi har en coverage-badge som viser hvor mye coverage testene har når de kjører

### GJENSTÅENDE EXTRAOPPGAVER

* [ ] Å opprette og liste spørsmål hadde vært logisk og REST-fult å gjøre med GET /api/questions og POST /api/questions. Klarer dere å endre måten dere håndterer controllers på slik at en GET og en POST request kan ha samme request target?
* [ ] Dersom noe alvorlig galt skjer vil serveren krasje. Serveren burde i stedet logge dette og returnere en status code 500 til brukeren
* [ ] Et favikon er et lite ikon som nettleseren viser i tab-vinduer for en webapplikasjon. Kan dere lage et favorittikon for deres server? Tips: ikonet er en binærfil og ikke en tekst og det går derfor ikke an å laste den inn i en StringBuilder
* [ ] I forelesningen har vi sett på å innføre begrepet Controllers for å organisere logikken i serveren. Unntaket fra det som håndteres med controllers er håndtering av filer på disk. Kan dere skrive om HttpServer til å bruke en FileController for å lese filer fra disk?
* [ ] JDBC koden fra forelesningen har en feil ved retrieve dersom id ikke finnes. Kan dere rette denne?
* [ ] I forelesningen fikk vi en rar feil med CSS når vi hadde <!DOCTYPE html>. Grunnen til det er feil content-type. Klarer dere å fikse det slik at det fungerer å ha <!DOCTYPE html> på starten av alle HTML-filer?
* [ ] I løpet av kurset har HttpServer og tester fått funksjonalitet som ikke lenger er nødvendig. Klarer dere å fjerne alt som er overflødig nå uten å også fjerne kode som fortsatt har verdi? (Advarsel: Denne kan trekke ned dersom dere gjør det feil!)
* [ ] Implementasjon av Chunked Transfer Encoding