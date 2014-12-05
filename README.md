Sudoku lahendaja
=================

Programmeerimisülesanne aines Tehisintellekt I (MTAT.06.008)

Sudokude lahendaja, mis suudab lahendada nii standardset 3x3 ruutudega sudokut kui ka muid nonomino kujudest koosnevaid sudokusid.

## Kompileerimine

Kõik vajalikud Java failid on src kaustas, käivitatav main meetod asub Main klassis. See kuidas failid kompileeritakse, ei oma tähtsust. Kui ei ole võimalust endal kompileerida, sisaldab repositoorium valmisehitatud versiooni viimasest koodist.

## Käivitamine

Tõmmatud JAR-i puhul kasutada käsurealt ``java -jar SudokuLahendaja.jar`` kaustas, kus antud fail asub või käivitada tavalisel viisil.

Käivitamisel kuvatakse dialoogaken kus saab valida millist tüüpi mõistatust lahendada või väljuda mängust.

Valides mõne ülesande lahendamise, palutakse kasutajal otsida üles fail, mis sisaldab algseisu. Kui valiti nonominode lahendaja, siis teiseks ka nende kujude faili pärast seda. Kui kõik läks edukalt, kuvatakse lahendatud mängu ruudustik ekraanile; vastasel juhul antakse kasutajale teada mis läks valesti.

Akna sulgedes avaneb taas esialgne aken, kus saab lahendada mõnda uut ülesannet.

## Algoritm

Algoritmi nuputasin välja ise, tuginedes varasemalt lahendatud 8 lipu probleemile ning ühele [selle aasta informaatika lahtise võistluse ülesandele](http://eio.ut.ee/uploads/Main/torn.pdf). Sisuliselt on tegu algoritmiga, mis kasutab nii tagasipöördumisega otsingut kui edasivaatavat kontrolli:

1) Hoidakse informatsiooni selle kohta, millisesse ritta, veergu või regiooni mõni arv on juba asetatud, et saaks kiirelt kontrollida stiilis "kas (reas|veerus|regioonis) R on asetatud juba arv N?"

2) Tühjadesse ruutudesse alustatakse ülevalt vasakult arvu 1 sisestamist, kui leitakse ruut, kus rea/veeru/regiooni suhtes on 1 asetamine lubatud, minnakse rekursiivselt edasi järgmisele reale sarnast ruutu otsima. Kui read saavad otsa, alustatakse ülevalt arvu 2 sisestamisega jne... Edukalt sisestatud rea korral märgitakse ära, et see arv on reas/veerus/regioonis võetud, et nendesse ei üritata sama arvu enam lisada.

3) Kui peaks tekkima olukord, kus arvu ei saa kindla rea peale asetada, minnakse tagasi eelmisele reale järgmist varianti proovima, eelnevalt vabastades selle arvu "võetud" staatus reast/veerust/regioonist. 

4) Lõppu jõudmisel saame tulemuseks lahendatud sudoku või teate ülesande mittelahenduvusest.

Tegu on lihtsa otsingualgoritmiga ning olemuselt üsna naiivne, kontrollitakse kas mõni käik rahuldab sudoku reegleid ilma otseselt tulevikku nägemata. 9x9 ruudustiku puhul ei ole rekursiivsel lahendusel väljakutsete sügavus väga suur ning seega lahenduse saab kätte üsna kiiresti. Suuremate ruudustike puhul suureneb aga tööks vajalik aeg märgatavalt ning on tarvis võtteid, mis suudavad oskuslikumalt grupeerida või elimineerida olekuid, mistõttu ma ei deklareeri seda algoritmi intelligentseks.
