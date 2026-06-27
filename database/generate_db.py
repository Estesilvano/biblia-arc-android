#!/usr/bin/env python3
import json, sqlite3, os, re, urllib.request, urllib.parse, sys, glob

RAW_DIR = os.path.join(os.path.dirname(__file__), 'raw_js')
DB_PATH = os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'res', 'raw', 'biblia_arc.db')
GITHUB_RAW = "https://raw.githubusercontent.com/HelioGiroto/Biblia-ARC/master"

BOOKS_ARC = [
    (1,"Gênesis","Gn",0),(2,"Êxodo","Ex",0),(3,"Levítico","Lv",0),(4,"Números","Nm",0),
    (5,"Deuteronômio","Dt",0),(6,"Josué","Js",0),(7,"Juízes","Jz",0),(8,"Rute","Rt",0),
    (9,"1 Samuel","1Sm",0),(10,"2 Samuel","2Sm",0),(11,"1 Reis","1Rs",0),(12,"2 Reis","2Rs",0),
    (13,"1 Crônicas","1Cr",0),(14,"2 Crônicas","2Cr",0),(15,"Esdras","Ed",0),(16,"Neemias","Ne",0),
    (17,"Ester","Et",0),(18,"Jó","Jó",0),(19,"Salmos","Sl",0),(20,"Provérbios","Pv",0),
    (21,"Eclesiastes","Ec",0),(22,"Cânticos","Ct",0),(23,"Isaías","Is",0),(24,"Jeremias","Jr",0),
    (25,"Lamentações","Lm",0),(26,"Ezequiel","Ez",0),(27,"Daniel","Dn",0),(28,"Oseias","Os",0),
    (29,"Joel","Jl",0),(30,"Amós","Am",0),(31,"Obadias","Ob",0),(32,"Jonas","Jn",0),
    (33,"Miqueias","Mq",0),(34,"Naum","Na",0),(35,"Habacuque","Hc",0),(36,"Sofonias","Sf",0),
    (37,"Ageu","Ag",0),(38,"Zacarias","Zc",0),(39,"Malaquias","Ml",0),(40,"Mateus","Mt",1),
    (41,"Marcos","Mc",1),(42,"Lucas","Lc",1),(43,"João","Jo",1),(44,"Atos","At",1),
    (45,"Romanos","Rm",1),(46,"1 Coríntios","1Co",1),(47,"2 Coríntios","2Co",1),(48,"Gálatas","Gl",1),
    (49,"Efésios","Ef",1),(50,"Filipenses","Fp",1),(51,"Colossenses","Cl",1),(52,"1 Tessalonicenses","1Ts",1),
    (53,"2 Tessalonicenses","2Ts",1),(54,"1 Timóteo","1Tm",1),(55,"2 Timóteo","2Tm",1),(56,"Tito","Tt",1),
    (57,"Filemom","Fm",1),(58,"Hebreus","Hb",1),(59,"Tiago","Tg",1),(60,"1 Pedro","1Pe",1),
    (61,"2 Pedro","2Pe",1),(62,"1 João","1Jo",1),(63,"2 João","2Jo",1),(64,"3 João","3Jo",1),
    (65,"Judas","Jd",1),(66,"Apocalipse","Ap",1),
]

STRONGS = {
    "H7225": ("רֵאשִׁית","reshit","princípio, começo, primícias","hebrew"),
    "H430": ("אֱלֹהִים","elohim","Deus, juiz","hebrew"),
    "H8064": ("שָׁמַיִם","shamayim","céus, firmamento","hebrew"),
    "H776": ("אֶרֶץ","erets","terra, solo, país","hebrew"),
    "H8415": ("תְּהוֹם","tehom","abismo, profundezas","hebrew"),
    "H7307": ("רוּחַ","ruach","espírito, vento, sopro","hebrew"),
    "H216": ("אוֹר","or","luz","hebrew"),
    "H2896": ("טוֹב","tov","bom, agradável","hebrew"),
    "H3117": ("יוֹם","yom","dia","hebrew"),
    "H3915": ("לַיִל","layil","noite","hebrew"),
    "H1254": ("בָּרָא","bara","criar, formar","hebrew"),
    "H120": ("אָדָם","adam","homem, humanidade","hebrew"),
    "H802": ("אִשָּׁה","ishah","mulher, esposa","hebrew"),
    "H376": ("אִישׁ","ish","homem, marido","hebrew"),
    "H1": ("אָב","av","pai","hebrew"),
    "H1121": ("בֵּן","ben","filho","hebrew"),
    "H8104": ("שָׁמַר","shamar","guardar, observar","hebrew"),
    "H1288": ("בָּרַךְ","barak","abençoar","hebrew"),
    "H2398": ("חָטָא","chata","pecar, errar o alvo","hebrew"),
    "H3468": ("יֶשַׁע","yesha","salvação, livramento","hebrew"),
    "H3068": ("יְהֹוָה","YHWH","SENHOR, Javé, o Eterno","hebrew"),
    "H6664": ("צֶדֶק","tsedek","justiça, retidão","hebrew"),
    "H2617": ("חֶסֶד","chesed","misericórdia, amor leal","hebrew"),
    "H530": ("אֱמוּנָה","emunah","fé, fidelidade","hebrew"),
    "H7965": ("שָׁלוֹם","shalom","paz, completa","hebrew"),
    "H6944": ("קֹדֶשׁ","kodesh","santo, sagrado","hebrew"),
    "H559": ("אָמַר","amar","dizer, falar","hebrew"),
    "H6213": ("עָשָׂה","asah","fazer, produzir","hebrew"),
    "H7200": ("רָאָה","raah","ver, contemplar","hebrew"),
    "H8085": ("שָׁמַע","shama","ouvir, obedecer","hebrew"),
    "H3519": ("כָּבוֹד","kavod","glória, honra","hebrew"),
    "H6666": ("צְדָקָה","tsedaqah","justiça, retidão, caridade","hebrew"),
    "H8451": ("תּוֹרָה","torah","lei, instrução, Torá","hebrew"),
    "G3056": ("λόγος","logos","palavra, verbo","greek"),
    "G2316": ("θεός","theos","Deus","greek"),
    "G2962": ("κύριος","kyrios","Senhor","greek"),
    "G5547": ("Χριστός","christos","Cristo, Ungido","greek"),
    "G2424": ("Ἰησοῦς","Iesous","Jesus, Yeshua","greek"),
    "G4151": ("πνεῦμα","pneuma","Espírito, sopro","greek"),
    "G26": ("ἀγάπη","agape","amor","greek"),
    "G4100": ("πιστεύω","pisteuo","crer, confiar","greek"),
    "G5485": ("χάρις","charis","graça, favor","greek"),
    "G165": ("αἰών","aion","eternidade, século","greek"),
    "G2222": ("ζωή","zoe","vida","greek"),
    "G5457": ("φῶς","phos","luz","greek"),
    "G4655": ("σκότος","skotos","trevas","greek"),
    "G1343": ("δικαιοσύνη","dikaiosyne","justiça","greek"),
    "G1515": ("εἰρήνη","eirene","paz","greek"),
    "G1680": ("ἐλπίς","elpis","esperança","greek"),
    "G4102": ("πίστις","pistis","fé","greek"),
    "G40": ("ἅγιος","hagios","santo","greek"),
    "G932": ("βασιλεία","basileia","reino","greek"),
    "G225": ("ἀλήθεια","aletheia","verdade","greek"),
    "G4592": ("σημεῖον","semeion","sinal, milagre","greek"),
    "G1411": ("δύναμις","dynamis","poder","greek"),
    "G264": ("ἁμαρτάνω","hamartano","pecar","greek"),
    "G4991": ("σωτηρία","soteria","salvação","greek"),
    "G5207": ("υἱός","huios","filho","greek"),
    "G36": ("ἀγή","age","sem pai, impropriamente","greek"),
}

COMMENTARY = {
    "Gn1.1": ("Criação: Origem do Universo","O versículo inaugural da Bíblia estabelece a doutrina fundamental da criação ex nihilo. O verbo hebraico 'bara' é usado exclusivamente para atividade criadora divina. Este versículo refuta: (1) o ateísmo — Deus existe; (2) o panteísmo — Deus é distinto da criação; (3) o dualismo — Deus criou tudo sozinho. A Trindade está implícita: Deus (Pai), o Espírito (v.2), e a Palavra (Jo 1:1)."),
    "Gn3.15": ("Protoevangelho: Primeira Promessa de Redenção","Deus promete que a semente da mulher esmagará a cabeça da serpente, embora seu calcanhar seja ferido. Aponta para Cristo: a ferida no calcanhar é a crucificação; o esmagamento da cabeça é a vitória final sobre Satanás. Primeiro vislumbre do plano redentor, demonstrado antes mesmo da fundação do mundo (Ap 13:8)."),
    "Gn15.6": ("Justificação pela Fé","Abrão creu no SENHOR, e isso lhe foi imputado como justiça. Texto fundamental da doutrina da justificação pela fé, citado por Paulo em Rm 4 e Gl 3. Deus declara justo o pecador que confia em suas promessas. Abraão foi salvo da mesma forma que todo crente: pela graça, mediante a fé."),
    "Ex3.14": ("EU SOU: O Nome de Deus","'EU SOU O QUE SOU' — YHWH revela seu nome a Moisés, expressando auto-existência, eternidade e imutabilidade divina. Jesus aplicou este nome a si mesmo em João 8:58, declarando sua divindade. O nome testifica que Deus age na história e se revela ao seu povo."),
    "Sl23.1": ("O Bom Pastor","Davi expressa confiança em Deus usando a metáfora do pastor, representando cuidado, provisão e proteção. Jesus se declara o 'Bom Pastor' (Jo 10). O salmo traça a jornada do crente: descanso, provação ('vale da sombra da morte'), e vitória."),
    "Is53.5": ("O Sofrimento Substitutivo do Messias","Descrição profética da expiação vicária: Cristo não morreu como mero mártir, mas como substituto, levando o castigo que merecíamos. 'Por suas pisaduras fomos sarados' refere-se primariamente à cura espiritual (perdão). Quarto Cântico do Servo Sofredor."),
    "Jr29.11": ("Planos de Bem e de Paz","Deus assegura ao povo exilado que tem planos de bem e não de mal. Contexto: exílio babilônico. Deus promete restauração após 70 anos. Para o crente hoje: Deus opera todas as coisas para o bem (Rm 8:28)."),
    "Jo3.16": ("O Evangelho em Um Versículo","O amor de Deus é a fonte, a dádiva do Filho é o meio, e a fé é a condição para a vida eterna. 'Deus amou o mundo' — amor sacrificial. 'Deu seu Filho unigênito' — o custo do amor redentor. 'Todo aquele que nele crê' — universalidade da oferta, fé como meio."),
    "Rm3.23": ("A Condição Humana e a Solução Divina","'Todos pecaram' — necessidade universal do evangelho. 'Destituídos da glória de Deus' — perda da imagem divina. 'Justificados gratuitamente' — a solução está na redenção em Cristo Jesus, pura graça sem mérito humano."),
    "Rm8.28": ("O Propósito Soberano de Deus","Deus opera todas as circunstâncias para o bem daqueles que o amam. 'Todas as coisas' inclui provações. Deus não causa o mal, mas redime até o mal para cumprir seus propósitos. 'Bem' não é conforto pessoal, mas conformidade à imagem de Cristo."),
    "Ef2.8": ("Salvos pela Graça","Graça (charis) é o favor imerecido; fé (pistis) é a mão vazia que recebe o dom. Até a fé é dom de Deus. 'Não vem das obras' — exclusão total da autojustiça. 'Para que ninguém se glorie' — toda glória pertence a Deus."),
    "Hb11.1": ("A Natureza da Fé","A fé é a substância (hypostasis) das coisas que se esperam e a evidência das coisas que não se veem. A fé não é crença irracional, mas confiança fundamentada no caráter de Deus e em suas promessas. Capítulo 11 é a 'galeria dos heróis da fé'."),
    "Ap21.1": ("Novos Céus e Nova Terra","João vê a consumação final: a criação será renovada, não aniquilada. O mar (símbolo de caos) não mais existe. Deus habita com seu povo. 'Enxugará toda lágrima' — o fim de toda dor e morte. A história redentora culmina na comunhão perfeita com Deus."),
}

def parse_js_array(data):
    data = data.strip()
    if "livro = " in data:
        data = data.split("livro = ", 1)[-1]
    if data.endswith(";"):
        data = data[:-1]
    data = data.strip()
    data = re.sub(r',\s*\]', ']', data)
    data = re.sub(r',\s*\}', '}', data)
    try:
        arr = json.loads(data)
    except json.JSONDecodeError as e:
        print(f"  JSON error: {e}")
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
    filename = urllib.parse.quote(f"{abbr}.js", safe='')
    url = f"{GITHUB_RAW}/{filename}"
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        data = urllib.request.urlopen(req, timeout=120).read().decode("utf-8")
        return data
    except Exception as e:
        print(f"  ERRO: {e}")
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
            testament INTEGER NOT NULL
        );
        CREATE TABLE verses (
            id INTEGER PRIMARY KEY,
            book_id INTEGER NOT NULL,
            chapter INTEGER NOT NULL,
            verse INTEGER NOT NULL,
            text TEXT NOT NULL,
            FOREIGN KEY (book_id) REFERENCES books(id)
        );
        CREATE INDEX idx_verses_book_chapter ON verses(book_id, chapter);
        CREATE TABLE strongs (
            number TEXT PRIMARY KEY,
            word TEXT,
            transliteration TEXT,
            definition TEXT,
            language TEXT NOT NULL
        );
        CREATE TABLE commentaries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            verse_id INTEGER NOT NULL,
            title TEXT,
            text TEXT NOT NULL,
            FOREIGN KEY (verse_id) REFERENCES verses(id)
        );
        CREATE INDEX idx_commentaries_verse ON commentaries(verse_id);
        CREATE TABLE bookmarks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            verse_id INTEGER NOT NULL,
            created_at INTEGER NOT NULL,
            FOREIGN KEY (verse_id) REFERENCES verses(id)
        );
    """)

    for b in BOOKS_ARC:
        c.execute("INSERT INTO books VALUES (?,?,?,?)", b)

    book_map = {b[2]: b[0] for b in BOOKS_ARC}

    print("Baixando e processando livros...")
    verse_id = 0
    total_verses = 0

    for b in BOOKS_ARC:
        abbr = b[2]
        print(f"  {abbr} ({b[1]})...", end=" ", flush=True)
        data = fetch_book(abbr)
        if not data:
            print("FALHOU")
            continue
        chapters = parse_js_array(data)
        if not chapters:
            print("S/Cap")
            continue
        count = 0
        for ch_idx, chapter in enumerate(chapters, 1):
            for v_idx, verse_text in enumerate(chapter, 1):
                verse_id += 1
                c.execute(
                    "INSERT INTO verses (id, book_id, chapter, verse, text) VALUES (?,?,?,?,?)",
                    (verse_id, b[0], ch_idx, v_idx, verse_text)
                )
                count += 1
        conn.commit()
        total_verses += count
        print(f"{count}v")

    print("Strong's...")
    for num, (word, translit, defn, lang) in STRONGS.items():
        c.execute("INSERT OR REPLACE INTO strongs VALUES (?,?,?,?,?)",
                  (num, word, translit, defn, lang))
    conn.commit()
    print(f"  {len(STRONGS)} definições")

    print("Comentários...")
    verse_map = {}
    for row in c.execute("SELECT id, book_id, chapter, verse FROM verses"):
        bk = next(b[2] for b in BOOKS_ARC if b[0] == row[1])
        key = f"{bk}{row[2]}.{row[3]}"
        verse_map[key] = row[0]

    cnt = 0
    for ref, (title, text) in COMMENTARY.items():
        vid = verse_map.get(ref)
        if not vid:
            m = re.match(r'([A-Za-z0-9]+)(\d+)\.(\d+)', ref)
            if m:
                vid = verse_map.get(f"{m.group(1)}{m.group(2)}.{m.group(3)}")
        if vid:
            c.execute("INSERT INTO commentaries (verse_id, title, text) VALUES (?,?,?)", (vid, title, text))
            cnt += 1
    conn.commit()
    print(f"  {cnt} comentários")

    conn.close()

    size = os.path.getsize(DB_PATH)
    print(f"\nOK: {total_verses}v, {len(STRONGS)}s, {cnt}c | {size/1024:.0f}KB")

if __name__ == "__main__":
    main()
