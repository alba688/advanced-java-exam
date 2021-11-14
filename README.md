[![Java CI with Maven](https://github.com/kristiania-pgr203-2021/pgr203-exam-ssovesen/actions/workflows/maven.yml/badge.svg)](https://github.com/kristiania-pgr203-2021/pgr203-exam-ssovesen/actions/workflows/maven.yml)
![Coverage](doc/jacoco.svg)

# Eksamen - PGR203 - Avansert Java
### README.md skal inneholde:

* [ ] Korrekt link til Github Actions
* [x] Beskrivelse av prosjektets funksjonalitet, hvordan vi har bygget det
* [x] Beskrivelse av hvordan man kjører/skal teste programmet
* [ ] Beskrivelse eventuell ekstra leveranse utover minimum
* [ ] Et diagram som viser datamodellen
* [ ] Hvordan man bygger, konfigurerer og kjører løsningen
* [ ] Dokumentasjon av design på løsningen
* [ ] Beskrivelse av erfaringene med arbeidet og løsningen

## Funksjonalitet:

Programmet lar brukeren opprette en eller flere brukerundersøkelse med spørsmål og kategorier. Hvert spørsmål har en tittel og kan besvares på en skala som brukeren har bestemt når spørsmålet ble lagret. Brukeren kan i tillegg legge inn informasjon om seg selv, og registere sitt svar på en brukersøkelse. En kan også liste ut en spørreundersøkelse og se hva gjennomsnittsvaret er. Brukeren kan også slette eller redigere spørsmål om han ønsker.

På baksiden av programmet kjører en webserver som tar lister ut filer, håndterer HTTP forespørsler og som lagrer / og henter ut informasjon fra en database som vises til brukeren gjennom HTTP responser.
Vi begynte å bygge gjennom å lage en enkel klient som sender HTTP forespørsler, derettter opprettet vi en server-klasse som tar imot forespørslene og sender svar tilbake. Her sikret vi grunnleggende funksjonalitet som som å dele opp en forespørsel, ta imot informasjon gjennom querier, lese og sende data osv. Dette ble gjort i en egen HttpReader klasse som håndterer trafikken mellom klient og server. 

Det neste steget var å bygge et eller flere Data Access Objects som kunne håndtere operasjoner mot database: Lagre, slette, endre og liste ut informasjon. Til slutt bygde vi programmet med forskjellige Controllere som kontroller hva som skal skje på serveren når han mottar en forespørsel. 


## Hvordan kjøre programmet

Programmet trenger to filer for å kunne kjøre. En .jar fil som er selve programmet, og en properties fil som inneholder informasjon rundt din postgres-database.
Properties filen må hate pgr203.propertiest og inneholde følgende: 
```
dataSource.url= ...
dataSource.user= ...
dataSource.password= ...
```
Denne filen legges i samme mappe som .jar filen og må være tilstede når man kjører programmet. 
Programmet er bygget i en .jar fil som kan kjøres med åpne terminal og skrive kommandoen:
```java -jar pgr203-exam-ssovesen-1.0-SNAPSHOT.jar```

Nå skal programmet kjøre og du kan gå i browser'en og skrive inn ```http://localhost/10001``` og da skal du kunne begynne å bruke programmet. 

Møter du på problemer for å legge inn bruker, kan det være lurt å tømme browseren for cookies.

## ERFARINGEN VÅR
Vi har jobbet veldig hardt med dette prosjektet og er veldig stolte over hvor vi har havnet. Siden vi har jobbet sammen på tidligere arbeidskrav har vi skapt veldig god teamarbeid, tillit, og kommunikasjon som har vært svært viktig under eksamen. Vi begynte å jobbe før github-repoet ble publisert ved å lese nøye gjennom oppgaveteksten og diskutere hvordan programmet skulle fungere. Vi også begynte å tegne UML-diagram over hvordan databasen skulle se ut. Da hadde vi et god utgangspunkt for å begynne å kode. Vi par-programmert med TDD for å opprette nesten hele prosjektet. Når vi hadde den grunnleggende funksjonalitet, begynte vi å dele arbeid i vår egne prosjekter, som å lage ny funksjonalitet eller refactoring av koden.

## Diagram av programmet
UML diagram over databsen og hvordan vi har strukturert informasjonen. 

![UML diagram of data](/doc/UML.png)

Dette er et diagram som viser hvordan programmet vårt fungerer.

![PUML diagram of program](/doc/PUML.png)


### EXTRAOPPGAVER VI HAR KLART
* [x] Vi har et avansert datamoddell med totalt 5 tabeller
* [x] Vi har avansert funksjonalitet ved at hvert svaralternativet besvares på en skala 
* [x] Brukeren bestemmer skalaen og merkelappen på skalaen vet bruk av high_value og low_value
* [x] Et sett med spørsmål kan knyttes sammen til en kategori som knyttes til en spørreundersøkelse
* [x] Når brukeren besvarer en spørreundersøkelse registreres fornavn, etternavn og epost
* [x] Implementert Cookies i showQuestionnaire.html sånn at browseren husker hvem det er som svarer på Questionnaires. Dette også lagres i databasen med en Person Id. Dette kan eventuelt brukes til å vise alle besvarelse av en person.
* [x] Vi bruker abstraksjon i både DAO og Controller-klassene. Vi har en AbstractDao klasse som er brukt av alle Dao-klassene og en HttpController med handle() metode som må brukes av alle Controller-klassene.
* [x] Vi har brukt HTTP response 303 for å sende brukeren tilbake til forrige side etter de har utført en POST 
* [x] Vi har håndtert riktig encoding hvis brukeren skriver på norsk med bruk av URLDecorder i HttpReader når action=POST blir fulført og at hver html-side har <html lang="no"> da input blir håndtert riktig.
* [x] I tilfelle brukeren setter in feil port eller request target "/" får men likevel innholdet av index.html istedenfor 404
* [x] Vi har rammeverk rundt http-håndtering med HttpReader, HttpClient, HttpPostClient
* [x] Vi har laget diagram av hvordan programmet virker samt UML diagram av database tabellene
* [x] Hvis serveren krasje, det blir logget og returnerer en status code 500 til brukeren
* [x] Vi har laget et favicon til servern vår
* [x] Vi har skrivet om HttpServer til å bruke en FileController for å lese filer fra disk
* [x] Vi har refaktorert og fjernet tidligere tester og klasser som var overflødig uten å også fjerne kode som fortsatt har verdi
* [x] Vi har fikset content-type sånn at CSS fungerer med html-filer som har <!DOCTYPE html>


### GJENSTÅENDE EXTRAOPPGAVER

* [-] Å opprette og liste spørsmål hadde vært logisk og REST-fult å gjøre med GET /api/questions og POST /api/questions. Klarer dere å endre måten dere håndterer controllers på slik at en GET og en POST request kan ha samme request target?
* [x] JDBC koden fra forelesningen har en feil ved retrieve dersom id ikke finnes. Kan dere rette denne?
* [-] Implementasjon av Chunked Transfer Encoding
* [-] Vi har en coverage-badge som viser hvor mye coverage testene har når de kjører


- UML diagrammet er ikke 100%
- Vi må skrive inn hva vi ikke føler er 100%