# Informationen
Hallo Blinde Bonbons,

dies hier ist euer Git-Repository, welches ihr im Rahmen des Hauptprojekts im Softwareentwicklungspraktikums nutzen könnt und sollt. Im derzeitigen Zustand befindet sich in diesem dieses Readme, welches ihr grade lest, und ein [.gitignore](https://git-scm.com/docs/gitignore)-File.

## Handhabung

Eine sehr einfach und verständliche Anleitung zum Thema "git" findet ihr unter folgender Adresse:  https://rogerdudler.github.io/git-guide/index.de.html (Bitte schaut euch diese mindestens einmal an!)

### Download
* Git für eure Kommandozeile könnt ihr euch hier herunterladen: https://git-scm.com/downloads
* Als Plugin für Eclipse: https://www.eclipse.org/egit/
* Einbindung in IntelliJ: https://www.jetbrains.com/help/idea/using-git-integration.html


### Einstellungen

    git config --global user.name "Vorname Nachname"
    git config --global user.email "name@cip.ifi.lmu.de"

### Klonen des Repositorys
Die Adresse eures Repositorys findet ihr oben rechts auf dieser Seite, wenn ihr auf den blauen `Clone` Knopf klickt. 
Auf euren Computer bekommt ihr dieses dann, indem ihr im gewünschten Verzeichnis folgenden Befehl aufruft:

    
    git clone git@gitlab2.cip.ifi.lmu.de:dbs_sep/dbs_sep2021/mobile-mauspads.git

Dies setzt einen von euch erstellten SSH Schlüssel voraus. Informiert euch bitte, wie ihr dies mit eurem Betriebssystem am Besten macht. Alternativ ist es auch möglich mit https zu arbeiten. 

## Keine Angst!
Git und GitLab bieten eine Menge an Funktionen. Wir möchten euch ermutigen diese zu erkunden und zu experimentieren. Solltet ihr Hemmungen haben an diesem Repository zu werkeln, könnt ihr euch auch gerne eigene Repositorys hier im GitLab erstellen und dort euer Wissen erweitern.

Solltet ihr im Laufe des Praktikums dazu entscheiden mit Branches zu arbeiten, würden wir euch bitten diese nicht mutwillig zu löschen. Um die schlimmsten Unfälle zu verhindern, ist in diesem Repository das Löschen des `master` Branches sowie `git push --force` auf diesem Branch nicht möglich. Falls ihr zusätzliche Branches geschützt haben möchtet, wendet euch bitte an euren Tutor.


