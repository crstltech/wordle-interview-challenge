package wordle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Mock Dictionary Service
 *
 * This service is working correctly — do not modify.
 * Simulates an external dictionary API with network latency.
 */
public class DictionaryService {

    private static final Set<String> DICTIONARY = Set.of(
        "APPLE", "ABOUT", "ABOVE", "ABUSE", "ACTOR",
        "ACUTE", "ADMIT", "ADOPT", "ADULT", "AGENT",
        "AGREE", "AHEAD", "ALARM", "ALBUM", "ALERT",
        "ALIKE", "ALIVE", "ALLOW", "ALONE", "ALONG",
        "ANGRY", "APART", "ARENA", "ARGUE", "ARISE",
        "AVOID", "AWARD", "BEACH", "BEGAN", "BEGIN",
        "BEING", "BELOW", "BIRTH", "BLACK", "BLAME",
        "BLANK", "BLIND", "BLOCK", "BLOOD", "BOARD",
        "BRAIN", "BRAVE", "BREAD", "BREAK", "BREED",
        "BRIEF", "BRING", "BROAD", "BROWN", "BUILD",
        "BUYER", "CARRY", "CATCH", "CAUSE", "CHAIN",
        "CHAIR", "CHEAP", "CHECK", "CHEST", "CHIEF",
        "CHILD", "CHINA", "CLAIM", "CLASS", "CLEAN",
        "CLEAR", "CLIMB", "CLOCK", "CLOSE", "CLOUD",
        "COACH", "COAST", "COULD", "COUNT", "COURT",
        "COVER", "CRAFT", "CRANE", "CRASH", "CREAM",
        "CREEP", "CRIME", "CROSS", "CROWD", "CROWN",
        "CYCLE", "DANCE", "DEATH", "DEPTH", "DOING",
        "DOUBT", "DRAFT", "DRAMA", "DRANK", "DREAM",
        "DRESS", "DRINK", "DRIVE", "DROVE", "DYING",
        "EARLY", "EARTH", "EIGHT", "ELITE", "EMPTY",
        "ENEMY", "ENJOY", "ENTER", "ENTRY", "EQUAL",
        "ERROR", "EVENT", "EVERY", "EXACT", "EXIST",
        "EXTRA", "FAITH", "FALSE", "FAULT", "FIBER",
        "FIELD", "FIFTH", "FIFTY", "FIGHT", "FINAL",
        "FIRST", "FIXED", "FLAME", "FLASH", "FLEET",
        "FLESH", "FLOAT", "FLOOD", "FLOOR", "FLOUR",
        "FLUID", "FOCUS", "FORCE", "FORTH", "FORTY",
        "FORUM", "FOUND", "FRAME", "FRANK", "FRAUD",
        "FRESH", "FRONT", "FRUIT", "FULLY", "FUNNY",
        "GHOST", "GIANT", "GIVEN", "GLASS", "GLOBE",
        "GOING", "GRACE", "GRADE", "GRAIN", "GRAND",
        "GRANT", "GRASS", "GRAVE", "GREAT", "GREEN",
        "GREET", "GRIEF", "GROSS", "GROUP", "GROWN",
        "GUARD", "GUESS", "GUEST", "GUIDE", "HAPPY",
        "HEART", "HEAVY", "HENCE", "HORSE", "HOTEL",
        "HOUSE", "HUMAN", "IDEAL", "IMAGE", "INDEX",
        "INNER", "INPUT", "ISSUE", "JAPAN", "JOINT",
        "JONES", "JUDGE", "JUICE", "KNIFE", "KNOCK",
        "KNOWN", "LABEL", "LARGE", "LASER", "LATER",
        "LAUGH", "LAYER", "LEARN", "LEASE", "LEAST",
        "LEAVE", "LEGAL", "LEVEL", "LIGHT", "LIMIT",
        "LOCAL", "LOOSE", "LOWER", "LUCKY", "LUNCH",
        "MAGIC", "MAJOR", "MAKER", "MARCH", "MARIA",
        "MATCH", "MAYBE", "MAYOR", "MEANT", "MEDIA",
        "METAL", "MIGHT", "MINOR", "MINUS", "MIXED",
        "MODEL", "MONEY", "MONTH", "MORAL", "MOTOR",
        "MOUNT", "MOUSE", "MOUTH", "MOVIE", "MUSIC",
        "NEEDS", "NEVER", "NEWLY", "NIGHT", "NOISE",
        "NORTH", "NOTED", "NOVEL", "NURSE", "OCCUR",
        "OCEAN", "OFFER", "OFTEN", "ORDER", "OTHER",
        "OUGHT", "PAINT", "PANEL", "PAPER", "PARTY",
        "PEACE", "PETER", "PHASE", "PHONE", "PHOTO",
        "PIECE", "PILOT", "PITCH", "PLACE", "PLAIN",
        "PLANE", "PLANT", "PLATE", "POINT", "POUND",
        "POWER", "PRESS", "PRICE", "PRIDE", "PRIME",
        "PRINT", "PRIOR", "PRIZE", "PROOF", "PROUD",
        "PROVE", "QUEEN", "QUICK", "QUIET", "QUITE",
        "RADIO", "RAISE", "RANGE", "RAPID", "RATIO",
        "REACH", "REACT", "READY", "REALM", "REFER",
        "REIGN", "RELAX", "REPLY", "RIGHT", "RIVER",
        "ROBOT", "ROGER", "ROMAN", "ROUGH", "ROUND",
        "ROUTE", "ROYAL", "RURAL", "SCALE", "SCENE",
        "SCOPE", "SCORE", "SENSE", "SERVE", "SEVEN",
        "SHALL", "SHAPE", "SHARE", "SHARP", "SHEEP",
        "SHEER", "SHEET", "SHELF", "SHELL", "SHIFT",
        "SHINE", "SHIRT", "SHOCK", "SHOOT", "SHORT",
        "SHOWN", "SIGHT", "SINCE", "SIXTH", "SIXTY",
        "SIZED", "SKILL", "SLEEP", "SLIDE", "SLOPE",
        "SMALL", "SMART", "SMILE", "SMITH", "SMOKE",
        "SOLID", "SOLVE", "SORRY", "SOUND", "SOUTH",
        "SPACE", "SPARE", "SPEAK", "SPEED", "SPEND",
        "SPENT", "SPLIT", "SPOKE", "SPORT", "STAFF",
        "STAGE", "STAKE", "STAND", "START", "STATE",
        "STEAM", "STEEL", "STEEP", "STICK", "STILL",
        "STOCK", "STONE", "STOOD", "STORE", "STORM",
        "STORY", "STRIP", "STUCK", "STUDY", "STUFF",
        "STYLE", "SUGAR", "SUITE", "SUPER", "SWEET",
        "TABLE", "TAKEN", "TASTE", "TAXES", "TEACH",
        "TEETH", "TERRY", "TEXAS", "THANK", "THEFT",
        "THEIR", "THEME", "THERE", "THESE", "THICK",
        "THING", "THINK", "THIRD", "THOSE", "THREE",
        "THREW", "THROW", "TIGHT", "TIMES", "TIRED",
        "TITLE", "TODAY", "TOKEN", "TOPIC", "TOTAL",
        "TOUCH", "TOUGH", "TOWER", "TRACK", "TRADE",
        "TRAIN", "TRAIT", "TRASH", "TREAT", "TREND",
        "TRIAL", "TRIBE", "TRICK", "TRIED", "TROOP",
        "TRUCK", "TRULY", "TRUST", "TRUTH", "TWICE",
        "UNCLE", "UNDER", "UNION", "UNITY", "UNTIL",
        "UPPER", "UPSET", "URBAN", "USAGE", "USUAL",
        "VALID", "VALUE", "VIDEO", "VIRUS", "VISIT",
        "VITAL", "VOICE", "WASTE", "WATCH", "WATER",
        "WHEEL", "WHERE", "WHICH", "WHILE", "WHITE",
        "WHOLE", "WHOSE", "WOMAN", "WORLD", "WORRY",
        "WORSE", "WORST", "WORTH", "WOULD", "WOUND",
        "WRITE", "WRONG", "WROTE", "YIELD", "YOUNG",
        "YOUTH", "ZEBRA", "ZONES"
    );

    private static final List<String> WORD_LIST;

    static {
        WORD_LIST = new ArrayList<>(DICTIONARY);
        Collections.sort(WORD_LIST);
    }

    private final Random random = new Random();

    /**
     * Check if a word is valid (exists in dictionary).
     * Simulates network latency (10–50ms).
     */
    public boolean isValidWord(String word) {
        simulateLatency();
        return DICTIONARY.contains(word.toUpperCase());
    }

    /**
     * Get a random word from the dictionary.
     */
    public String getRandomWord() {
        return WORD_LIST.get(random.nextInt(WORD_LIST.size()));
    }

    /**
     * Get all words (useful for solver).
     */
    public List<String> getAllWords() {
        return Collections.unmodifiableList(WORD_LIST);
    }

    private void simulateLatency() {
        int delay = random.nextInt(40) + 10;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
