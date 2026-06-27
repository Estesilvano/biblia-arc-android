#!/usr/bin/env python3
"""Generate SQLite database with ARC Bible text + Strong's + commentary"""

import json, sqlite3, os, re, urllib.request, sys

DB_PATH = os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'res', 'raw', 'biblia_arc.db')

BOOKS_ARC = [
    (1,"Gênesis","Gn",0,1),(2,"Êxodo","Ex",0,2),(3,"Levítico","Lv",0,3),(4,"Números","Nm",0,4),
    (5,"Deuteronômio","Dt",0,5),(6,"Josué","Js",0,6),(7,"Juízes","Jz",0,7),(8,"Rute","Rt",0,8),
    (9,"1 Samuel","1Sm",0,9),(10,"2 Samuel","2Sm",0,10),(11,"1 Reis","1Rs",0,11),(12,"2 Reis","2Rs",0,12),
    (13,"1 Crônicas","1Cr",0,13),(14,"2 Crônicas","2Cr",0,14),(15,"Esdras","Ed",0,15),(16,"Neemias","Ne",0,16),
    (17,"Ester","Et",0,17),(18,"Jó","Jó",0,18),(19,"Salmos","Sl",0,19),(20,"Provérbios","Pv",0,20),
    (21,"Eclesiastes","Ec",0,21),(22,"Cânticos","Ct",0,22),(23,"Isaías","Is",0,23),(24,"Jeremias","Jr",0,24),
    (25,"Lamentações","Lm",0,25),(26,"Ezequiel","Ez",0,26),(27,"Daniel","Dn",0,27),(28,"Oseias","Os",0,28),
    (29,"Joel","Jl",0,29),(30,"Amós","Am",0,30),(31,"Obadias","Ob",0,31),(32,"Jonas","Jn",0,32),
    (33,"Miqueias","Mq",0,33),(34,"Naum","Na",0,34),(35,"Habacuque","Hc",0,35),(36,"Sofonias","Sf",0,36),
    (37,"Ageu","Ag",0,37),(38,"Zacarias","Zc",0,38),(39,"Malaquias","Ml",0,39),
    (40,"Mateus","Mt",1,40),(41,"Marcos","Mc",1,41),(42,"Lucas","Lc",1,42),(43,"João","Jo",1,43),
    (44,"Atos","At",1,44),(45,"Romanos","Rm",1,45),(46,"1 Coríntios","1Co",1,46),(47,"2 Coríntios","2Co",1,47),
    (48,"Gálatas","Gl",1,48),(49,"Efésios","Ef",1,49),(50,"Filipenses","Fp",1,50),(51,"Colossenses","Cl",1,51),
    (52,"1 Tessalonicenses","1Ts",1,52),(53,"2 Tessalonicenses","2Ts",1,53),(54,"1 Timóteo","1Tm",1,54),
    (55,"2 Timóteo","2Tm",1,55),(56,"Tito","Tt",1,56),(57,"Filemom","Fm",1,57),(58,"Hebreus","Hb",1,58),
    (59,"Tiago","Tg",1,59),(60,"1 Pedro","1Pe",1,60),(61,"2 Pedro","2Pe",1,61),(62,"1 João","1Jo",1,62),
    (63,"2 João","2Jo",1,63),(64,"3 João","3Jo",1,64),(65,"Judas","Jd",1,65),(66,"Apocalipse","Ap",1,66),
]

BOOK_ABBREVIATIONS = {b[2] for b in BOOKS_ARC}

GITHUB_RAW = "https://raw.githubusercontent.com/HelioGiroto/Biblia-ARC/master"

STRONGS_DATA = {
    "H7225": {"word":"רֵאשִׁית","translit":"reshit","def":"princípio, começo, primícias","lang":"hebrew"},
    "H430": {"word":"אֱלֹהִים","translit":"elohim","def":"Deus, juiz","lang":"hebrew"},
    "H8064": {"word":"שָׁמַיִם","translit":"shamayim","def":"céus, firmamento","lang":"hebrew"},
    "H776": {"word":"אֶרֶץ","translit":"erets","def":"terra, solo, país","lang":"hebrew"},
    "H8415": {"word":"תְּהוֹם","translit":"tehom","def":"abismo, profundezas","lang":"hebrew"},
    "H7307": {"word":"רוּחַ","translit":"ruach","def":"espírito, vento, sopro","lang":"hebrew"},
    "H216": {"word":"אוֹר","translit":"or","def":"luz","lang":"hebrew"},
    "H2896": {"word":"טוֹב","translit":"tov","def":"bom, agradável","lang":"hebrew"},
    "H3117": {"word":"יוֹם","translit":"yom","def":"dia","lang":"hebrew"},
    "H3915": {"word":"לַיִל","translit":"layil","def":"noite","lang":"hebrew"},
    "H1254": {"word":"בָּרָא","translit":"bara","def":"criar, formar","lang":"hebrew"},
    "H120": {"word":"אָדָם","translit":"adam","def":"homem, humanidade","lang":"hebrew"},
    "H802": {"word":"אִשָּׁה","translit":"ishah","def":"mulher, esposa","lang":"hebrew"},
    "H376": {"word":"אִישׁ","translit":"ish","def":"homem, marido","lang":"hebrew"},
    "H1": {"word":"אָב","translit":"av","def":"pai","lang":"hebrew"},
    "H1121": {"word":"בֵּן","translit":"ben","def":"filho","lang":"hebrew"},
    "H8104": {"word":"שָׁמַר","translit":"shamar","def":"guardar, observar","lang":"hebrew"},
    "H1288": {"word":"בָּרַךְ","translit":"barak","def":"abençoar","lang":"hebrew"},
    "H2398": {"word":"חָטָא","translit":"chata","def":"pecar, errar o alvo","lang":"hebrew"},
    "H3468": {"word":"יֶשַׁע","translit":"yesha","def":"salvação, livramento","lang":"hebrew"},
    "H3068": {"word":"יְהֹוָה","translit":"YHWH","def":"SENHOR, Javé, o Eterno","lang":"hebrew"},
    "H6664": {"word":"צֶדֶק","translit":"tsedek","def":"justiça, retidão","lang":"hebrew"},
    "H2617": {"word":"חֶסֶד","translit":"chesed","def":"misericórdia, amor leal","lang":"hebrew"},
    "H530": {"word":"אֱמוּנָה","translit":"emunah","def":"fé, fidelidade","lang":"hebrew"},
    "H7965": {"word":"שָׁלוֹם","translit":"shalom","def":"paz, completa","lang":"hebrew"},
    "H6944": {"word":"קֹדֶשׁ","translit":"kodesh","def":"santo, sagrado","lang":"hebrew"},
    "H559": {"word":"אָמַר","translit":"amar","def":"dizer, falar","lang":"hebrew"},
    "H6213": {"word":"עָשָׂה","translit":"asah","def":"fazer, produzir","lang":"hebrew"},
    "H7200": {"word":"רָאָה","translit":"raah","def":"ver, contemplar","lang":"hebrew"},
    "H8085": {"word":"שָׁמַע","translit":"shama","def":"ouvir, obedecer","lang":"hebrew"},
    "H3519": {"word":"כָּבוֹד","translit":"kavod","def":"glória, honra","lang":"hebrew"},
    "H6666": {"word":"צְדָקָה","translit":"tsedaqah","def":"justiça, retidão, caridade","lang":"hebrew"},
    "H3555": {"word":"כָּוָה","translit":"kavah","def":"esperar, aguardar","lang":"hebrew"},
    "H8451": {"word":"תּוֹרָה","translit":"torah","def":"lei, instrução, Torá","lang":"hebrew"},
    "H6944": {"word":"קֹדֶשׁ","translit":"kodesh","def":"santo, sagrado","lang":"hebrew"},
    "G3056": {"word":"λόγος","translit":"logos","def":"palavra, verbo","lang":"greek"},
    "G2316": {"word":"θεός","translit":"theos","def":"Deus","lang":"greek"},
    "G2962": {"word":"κύριος","translit":"kyrios","def":"Senhor","lang":"greek"},
    "G5547": {"word":"Χριστός","translit":"christos","def":"Cristo, Ungido","lang":"greek"},
    "G2424": {"word":"Ἰησοῦς","translit":"Iesous","def":"Jesus, Yeshua","lang":"greek"},
    "G4151": {"word":"πνεῦμα","translit":"pneuma","def":"Espírito, sopro","lang":"greek"},
    "G26": {"word":"ἀγάπη","translit":"agape","def":"amor","lang":"greek"},
    "G4100": {"word":"πιστεύω","translit":"pisteuo","def":"crer, confiar","lang":"greek"},
    "G5485": {"word":"χάρις","translit":"charis","def":"graça, favor","lang":"greek"},
    "G165": {"word":"αἰών","translit":"aion","def":"eternidade, século","lang":"greek"},
    "G2222": {"word":"ζωή","translit":"zoe","def":"vida","lang":"greek"},
    "G5457": {"word":"φῶς","translit":"phos","def":"luz","lang":"greek"},
    "G4655": {"word":"σκότος","translit":"skotos","def":"trevas","lang":"greek"},
    "G1343": {"word":"δικαιοσύνη","translit":"dikaiosyne","def":"justiça","lang":"greek"},
    "G1515": {"word":"εἰρήνη","translit":"eirene","def":"paz","lang":"greek"},
    "G1680": {"word":"ἐλπίς","translit":"elpis","def":"esperança","lang":"greek"},
    "G4102": {"word":"πίστις","translit":"pistis","def":"fé","lang":"greek"},
    "G40": {"word":"ἅγιος","translit":"hagios","def":"santo","lang":"greek"},
    "G932": {"word":"βασιλεία","translit":"basileia","def":"reino","lang":"greek"},
    "G225": {"word":"ἀλήθεια","translit":"aletheia","def":"verdade","lang":"greek"},
    "G4592": {"word":"σημεῖον","translit":"semeion","def":"sinal, milagre","lang":"greek"},
    "G1411": {"word":"δύναμις","translit":"dynamis","def":"poder","lang":"greek"},
    "G36": {"word":"ἀγή","translit":"age","def":"sem pai, impropriamente","lang":"greek"},
    "G264": {"word":"ἁμαρτάνω","translit":"hamartano","def":"pecar","lang":"greek"},
    "G4991": {"word":"σωτηρία","translit":"soteria","def":"salvação","lang":"greek"},
    "G5207": {"word":"υἱός","translit":"huios","def":"filho","lang":"greek"},
}

COMMENTARY = {
    "Gn1.1": {
        "general": "O versículo inaugural da Bíblia estabelece a doutrina fundamental da criação ex nihilo (criação a partir do nada). Deus, na soberania da sua vontade, traz à existência tudo o que há. O verbo hebraico 'bara' (criar) é usado exclusivamente para atividade criadora divina, indicando que somente Deus pode criar a partir do nada.",
        "theological": "Este versículo refuta três filosofias: (1) o ateísmo — pois declara que Deus existe; (2) o panteísmo — pois Deus é distinto da sua criação; (3) o dualismo — pois Deus criou tudo sozinho, sem matéria eterna ao seu lado. A Trindade está implícita: Deus (Pai), o Espírito (v.2), e a Palavra (João 1:1).",
        "context": "Gênesis (do grego 'origem') serve como fundamento para toda a Escritura. Aqui aprendemos a origem do universo, da vida, do homem, do pecado e da promessa de redenção."
    },
    "Gn1.26-27": {
        "general": "O plural 'Façamos' sugere a consulta intra-trinitária. O homem é criado à imagem e semelhança de Deus, implicando racionalidade, moralidade e capacidade de relacionamento com Deus e domínio sobre a criação.",
        "theological": "A imago Dei (imagem de Deus) é a doutrina que distingue o ser humano de toda a outra criação. Esta imagem, embora maculada pelo pecado, não foi perdida totalmente (Tiago 3:9). Sua restauração perfeita ocorre em Cristo (Cl 1:15).",
        "context": "A criação do homem é o ápice da obra criadora de Deus nos seis dias. Diferente dos animais, o homem foi criado para um relacionamento único com seu Criador."
    },
    "Gn3.15": {
        "general": "Este é o Protoevangelho (primeiro evangelho). Deus promete que a semente da mulher esmagará a cabeça da serpente (Satanás), embora o calcanhar do Messias seja ferido (a cruz).",
        "theological": "Primeiro vislumbre do plano redentor. A semente da mulher aponta para Jesus Cristo, nascido de virgem. A ferida no calcanhar refere-se à crucificação; o esmagamento da cabeça refere-se à vitória final de Cristo sobre Satanás.",
        "context": "Deus pronuncia juízo sobre a serpente imediatamente após a queda, demonstrando que a redenção estava planejada antes mesmo da fundação do mundo (Ap 13:8)."
    },
    "Gn15.6": {
        "general": "Abrão creu no SENHOR, e isso lhe foi imputado como justiça. Texto fundamental da doutrina da justificação pela fé, citado por Paulo em Rm 4 e Gl 3.",
        "theological": "A justificação não é por obras, mas pela fé. Deus declara justo o pecador que confia em suas promessas. Abraão foi salvo da mesma forma que todo crente: pela graça, mediante a fé. A justificação é imputada — creditada à nossa conta por causa de Cristo.",
        "context": "Deus acaba de prometer a Abraão descendência inumerável. A fé de Abraão não era genérica, mas fé na promessa específica de Deus."
    },
    "Ex3.14": {
        "general": "'EU SOU O QUE SOU' — YHWH revela seu nome a Moisés. Este nome expressa a auto-existência, eternidade e imutabilidade de Deus.",
        "theological": "Jesus aplicou este nome a si mesmo em João 8:58 ('Antes que Abraão existisse, EU SOU'), declarando sua divindade. O nome YHWH testifica que Deus é o Deus que age na história e se revela ao seu povo.",
        "context": "Moisés pergunta o nome de Deus para responder aos israelitas no Egito. Deus revela não apenas seu nome, mas seu caráter — o Deus da aliança que cumpre suas promessas."
    },
    "Sl23.1-6": {
        "general": "Davi expressa sua confiança em Deus usando a metáfora do pastor. A relação pastor-ovelha representa cuidado, provisão e proteção divinos.",
        "theological": "Jesus se declara o 'Bom Pastor' (Jo 10). O Salmo 23 traça a jornada do crente: descanso (v.2-3), provação (v.4), e vitória (v.5-6). 'Ainda que eu ande pelo vale da sombra da morte' — a fé persevera mesmo nas maiores adversidades.",
        "context": "Davi, que fora pastor antes de rei, usa sua experiência pessoal para ilustrar o cuidado de Deus. O Salmo contrasta com os Salmos de lamento que o precedem."
    },
    "Is53.5": {
        "general": "Descrição profética do sofrimento substitutivo do Messias. 'Ferido por nossas transgressões' — o Messias sofreria em nosso lugar, levando o castigo que merecíamos.",
        "theological": "A doutrina da expiação vicária está clara: Cristo não morreu como mero mártir, mas como substituto. O castigo que nos traz a paz estava sobre ele. 'Por suas pisaduras fomos sarados' — cura espiritual (perdão) está primariamente em vista.",
        "context": "Este é o quarto Cântico do Servo Sofredor de Isaías. O Servo é descrito como desfigurado, rejeitado, e finalmente morto pelos pecados do povo."
    },
    "Jr29.11": {
        "general": "Deus assegura ao seu povo exilado que tem planos de bem e não de mal, planos de dar-lhes futuro e esperança.",
        "theological": "O contexto é o exílio babilônico. Deus promete restauração após 70 anos. A verdade maior permanece: Deus é soberano e fiel às suas alianças. Para o crente hoje, a certeza é que Deus opera todas as coisas para o bem (Rm 8:28).",
        "context": "Jeremias escreve aos exilados em Babilônia. Eles deveriam buscar a paz da cidade onde estavam exilados, confiando no timing soberano de Deus para a restauração."
    },
    "Jo3.16": {
        "general": "O versículo que resume o evangelho: o amor de Deus é a fonte, a dádiva do Filho é o meio, e a fé é a condição para a vida eterna.",
        "theological": "'Deus amou o mundo' — amor sacrificial e eficaz. 'Deu seu Filho unigênito' — o custo do amor redentor. 'Para que todo aquele que nele crê' — universalidade da oferta, fé como meio. 'Não pereça, mas tenha a vida eterna' — o contraste entre perdição e salvação.",
        "context": "Jesus dialoga com Nicodemos, um fariseu e membro do Sinédrio. Este versículo conclui a explicação de Jesus sobre o novo nascimento e a obra do Filho do Homem."
    },
    "Rm3.23-24": {
        "general": "Paulo declara: (1) todos pecaram e estão destituídos da glória de Deus; (2) a justificação é gratuita pela redenção em Cristo Jesus.",
        "theological": "O versículo estabelece a necessidade universal do evangelho. 'Destituídos da glória de Deus' refere-se à perda da imagem divina. A solução está na 'redenção que há em Cristo Jesus'. A salvação é pura graça, sem mérito humano.",
        "context": "Paulo conclui sua argumentação de que judeus e gentios estão igualmente sob pecado (Rm 1:18-3:20). Agora apresenta a solução: a justificação pela fé."
    },
    "Rm8.28": {
        "general": "Deus está soberanamente no controle de todas as circunstâncias, operando-as para o bem daqueles que o amam e são chamados segundo seu propósito.",
        "theological": "'Todas as coisas' inclui provações e sofrimentos. Deus não causa o mal, mas em sua onipotência redime até o mal para cumprir seus propósitos. O 'bem' não é conforto pessoal, mas nossa conformidade à imagem de Cristo (v.29).",
        "context": "Paulo contrasta a presente fraqueza e sofrimento com a glória futura que será revelada. A certeza da soberania de Deus sustenta o crente em meio às aflições."
    },
    "Ef2.8-9": {
        "general": "A síntese paulina da salvação: graça é a fonte, fé é o meio, salvação é o dom. Exclui completamente o mérito humano.",
        "theological": "Graça (charis) é o favor imerecido de Deus. Fé (pistis) não é obra meritória, mas a mão vazia que recebe o dom. Até a fé é dom de Deus. 'Não vem das obras' — exclusão total da autojustiça. 'Para que ninguém se glorie' — toda glória pertence a Deus.",
        "context": "Paulo descreve a condição prévia do crente: 'morto em ofensas e pecados'. A salvação é apresentada como ressurreição espiritual, da morte para a vida em Cristo."
    },
    "Hb11.1": {
        "general": "A fé é a substância (realidade) das coisas que se esperam e a evidência (prova) das coisas que não se veem.",
        "theological": "A fé tem dois aspectos: (1) certeza do futuro ('coisas que se esperam'); (2) convicção do invisível ('coisas que não se veem'). A fé (pistis) não é crença irracional, mas confiança fundamentada no caráter de Deus e em suas promessas. Hypostasis = substância, fundamento.",
        "context": "O capítulo 11 de Hebreus é a 'galeria dos heróis da fé', listando exemplos do Antigo Testamento que viveram pela fé, confiando nas promessas de Deus."
    },
    "Ap21.1-4": {
        "general": "João vê a consumação final da redenção: novos céus e nova terra. A criação será renovada, não aniquilada.",
        "theological": "A escatologia bíblica culmina na redenção do cosmos, não em sua destruição. O mar (símbolo do caos e separação) não mais existe. A Nova Jerusalém desce do céu — Deus habita com seu povo. 'Enxugará toda lágrima' — o fim de toda dor e morte.",
        "context": "Esta é a visão final do Apocalipse, após o juízo final. A história redentora culmina na comunhão perfeita e eterna entre Deus e seu povo."
    },
}

def cleanup_json(data):
    data = data.strip()
    parts = data.split("livro = ")
    if len(parts) > 1:
        data = "livro = " + parts[-1]
    if data.startswith("livro = "):
        data = data[8:]
    if data.endswith(";"):
        data = data[:-1]
    data = data.strip()
    data = re.sub(r',\s*\]', ']', data)
    data = re.sub(r',\s*\}', '}', data)
    return data

def parse_book_arc(data):
    data = cleanup_json(data)
    try:
        arr = json.loads(data)
    except json.JSONDecodeError as e:
        print(f"    JSON error: {e}")
        return []
    
    if not arr or len(arr) < 2:
        return []
    
    chapters = []
    for item in arr[1:]:
        if isinstance(item, list) and len(item) > 1:
            verses = []
            for v in item[1:]:
                if isinstance(v, list) and len(v) > 0 and isinstance(v[0], str):
                    t = v[0].strip()
                    if t and t != " ":
                        verses.append(t)
            if verses:
                chapters.append(verses)
    return chapters

def fetch_book(abbr):
    import urllib.parse
    filename = urllib.parse.quote(f"{abbr}.js", safe='')
    url = f"{GITHUB_RAW}/{filename}"
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        data = urllib.request.urlopen(req, timeout=120).read().decode("utf-8")
        return data
    except Exception as e:
        print(f"  ERRO ao baixar {abbr}: {e}")
        return None

def main():
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)
    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)
    
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    
    c.executescript("""
        CREATE TABLE books (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            abbreviation TEXT NOT NULL,
            testament INTEGER NOT NULL,
            book_order INTEGER NOT NULL
        );
        CREATE TABLE verses (
            id INTEGER PRIMARY KEY,
            book_id INTEGER NOT NULL,
            chapter INTEGER NOT NULL,
            verse INTEGER NOT NULL,
            text TEXT NOT NULL,
            FOREIGN KEY (book_id) REFERENCES books(id)
        );
        CREATE INDEX idx_verses_book_chapter ON verses(book_id, chapter, verse);
        CREATE TABLE strongs (
            number TEXT PRIMARY KEY,
            word TEXT,
            transliteration TEXT,
            definition TEXT,
            language TEXT NOT NULL
        );
        CREATE TABLE commentaries (
            id INTEGER PRIMARY KEY,
            verse_id INTEGER NOT NULL,
            title TEXT,
            text TEXT NOT NULL,
            type TEXT DEFAULT 'general',
            FOREIGN KEY (verse_id) REFERENCES verses(id)
        );
        CREATE INDEX idx_commentaries_verse ON commentaries(verse_id);
        CREATE TABLE bookmarks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            verse_id INTEGER NOT NULL,
            type TEXT NOT NULL,
            color TEXT,
            note TEXT,
            created_at INTEGER NOT NULL,
            FOREIGN KEY (verse_id) REFERENCES verses(id)
        );
    """)
    
    for b in BOOKS_ARC:
        c.execute("INSERT INTO books VALUES (?,?,?,?,?)", b)
    
    book_map = {b[2]: b[0] for b in BOOKS_ARC}
    
    print("Baixando e processando livros...")
    verse_id = 0
    
    for abbr in sorted(BOOK_ABBREVIATIONS, key=lambda x: book_map[x]):
        book_id = book_map[abbr]
        name = next(b[1] for b in BOOKS_ARC if b[0] == book_id)
        print(f"  {abbr} ({name})...", end=" ", flush=True)
        
        data = fetch_book(abbr)
        if not data:
            print("FALHOU")
            continue
        
        chapters = parse_book_arc(data)
        if not chapters:
            print("S/B capítulos")
            continue
        
        count = 0
        for ch_idx, chapter in enumerate(chapters, 1):
            for v_idx, verse_text in enumerate(chapter, 1):
                verse_id += 1
                c.execute(
                    "INSERT INTO verses (id, book_id, chapter, verse, text) VALUES (?,?,?,?,?)",
                    (verse_id, book_id, ch_idx, v_idx, verse_text)
                )
                count += 1
        conn.commit()
        print(f"{count} versículos")
    
    print("Inserindo dados de Strong's...")
    for num, data in STRONGS_DATA.items():
        c.execute(
            "INSERT OR REPLACE INTO strongs VALUES (?,?,?,?,?)",
            (num, data["word"], data["translit"], data["def"], data["lang"])
        )
    conn.commit()
    
    print("Inserindo comentários teológicos...")
    verse_map = {}
    for row in c.execute("SELECT id, book_id, chapter, verse FROM verses").fetchall():
        bk = next(b[2] for b in BOOKS_ARC if b[0] == row[1])
        if row[2] == 1:
            key = f"{bk}{row[2]}.{row[3]}"
        else:
            key = f"{bk}{row[2]}.{row[3]}"
        key = f"{bk}{row[2]}.{row[3]}"
        verse_map[key] = row[0]
    
    for ref, content in COMMENTARY.items():
        parts = ref.split(".")
        bk_part = ref 
        if "-" in bk_part:
            parts = ref.split("-")
            ref_start = parts[0]
            ref_end = parts[1]
            vid_start = verse_map.get(ref_start)
            vid_end = verse_map.get(ref_end)
            if vid_start and vid_end:
                vid_start = vid_start
            else:
                continue
        
        vid = verse_map.get(ref)
        if not vid:
            bk_abbr = ref.rstrip("0123456789.")
            import re as re2
            match = re2.match(r'([A-Za-z0-9]+)(\d+)\.(\d+)', ref)
            if match:
                alt_key = f"{match.group(1)}{match.group(2)}.{match.group(3)}"
                vid = verse_map.get(alt_key)
            if not vid:
                print(f"  Versículo não encontrado: {ref}")
                continue
        
        title = content.get("title", "Comentário")
        for ctype, text in content.items():
            if ctype == "title":
                continue
            c.execute(
                "INSERT INTO commentaries (verse_id, title, text, type) VALUES (?,?,?,?)",
                (vid, title, text, ctype)
            )
    
    conn.commit()
    
    stats = {
        "books": c.execute("SELECT COUNT(*) FROM books").fetchone()[0],
        "verses": c.execute("SELECT COUNT(*) FROM verses").fetchone()[0],
        "strongs": c.execute("SELECT COUNT(*) FROM strongs").fetchone()[0],
        "commentaries": c.execute("SELECT COUNT(*) FROM commentaries").fetchone()[0],
    }
    conn.close()
    
    print(f"\n=== Database gerada ===")
    print(f"  Livros: {stats['books']}")
    print(f"  Versículos: {stats['verses']}")
    print(f"  Strong's: {stats['strongs']}")
    print(f"  Comentários: {stats['commentaries']}")
    print(f"  Tamanho: {os.path.getsize(DB_PATH)/1024:.1f} KB")
    print(f"  Path: {DB_PATH}")

if __name__ == "__main__":
    main()
