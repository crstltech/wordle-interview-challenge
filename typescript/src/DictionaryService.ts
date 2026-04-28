/**
 * Mock Dictionary Service
 * 
 * This service is working correctly — do not modify.
 * Simulates an async dictionary API.
 */

const DICTIONARY: Set<string> = new Set([
  // Common 5-letter words
  'APPLE', 'ABOUT', 'ABOVE', 'ABUSE', 'ACTOR',
  'ACUTE', 'ADMIT', 'ADOPT', 'ADULT', 'AGENT',
  'AGREE', 'AHEAD', 'ALARM', 'ALBUM', 'ALERT',
  'ALIKE', 'ALIVE', 'ALLOW', 'ALONE', 'ALONG',
  'ANGRY', 'APART', 'ARENA', 'ARGUE', 'ARISE',
  'AVOID', 'AWARD', 'BEACH', 'BEGAN', 'BEGIN',
  'BEING', 'BELOW', 'BIRTH', 'BLACK', 'BLAME',
  'BLANK', 'BLIND', 'BLOCK', 'BLOOD', 'BOARD',
  'BRAIN', 'BRAVE', 'BREAD', 'BREAK', 'BREED',
  'BRIEF', 'BRING', 'BROAD', 'BROWN', 'BUILD',
  'BUYER', 'CARRY', 'CATCH', 'CAUSE', 'CHAIN',
  'CHAIR', 'CHEAP', 'CHECK', 'CHEST', 'CHIEF',
  'CHILD', 'CHINA', 'CLAIM', 'CLASS', 'CLEAN',
  'CLEAR', 'CLIMB', 'CLOCK', 'CLOSE', 'CLOUD',
  'COACH', 'COAST', 'COULD', 'COUNT', 'COURT',
  'COVER', 'CRAFT', 'CRANE', 'CRASH', 'CREAM',
  'CREEP', 'CRIME', 'CROSS', 'CROWD', 'CROWN',
  'CYCLE', 'DANCE', 'DEATH', 'DEPTH', 'DOING',
  'DOUBT', 'DRAFT', 'DRAMA', 'DRANK', 'DREAM',
  'DRESS', 'DRINK', 'DRIVE', 'DROVE', 'DYING',
  'EARLY', 'EARTH', 'EIGHT', 'ELITE', 'EMPTY',
  'ENEMY', 'ENJOY', 'ENTER', 'ENTRY', 'EQUAL',
  'ERROR', 'EVENT', 'EVERY', 'EXACT', 'EXIST',
  'EXTRA', 'FAITH', 'FALSE', 'FAULT', 'FIBER',
  'FIELD', 'FIFTH', 'FIFTY', 'FIGHT', 'FINAL',
  'FIRST', 'FIXED', 'FLAME', 'FLASH', 'FLEET',
  'FLESH', 'FLOAT', 'FLOOD', 'FLOOR', 'FLOUR',
  'FLUID', 'FOCUS', 'FORCE', 'FORTH', 'FORTY',
  'FORUM', 'FOUND', 'FRAME', 'FRANK', 'FRAUD',
  'FRESH', 'FRONT', 'FRUIT', 'FULLY', 'FUNNY',
  'GHOST', 'GIANT', 'GIVEN', 'GLASS', 'GLOBE',
  'GOING', 'GRACE', 'GRADE', 'GRAIN', 'GRAND',
  'GRANT', 'GRASS', 'GRAVE', 'GREAT', 'GREEN',
  'GREET', 'GRIEF', 'GROSS', 'GROUP', 'GROWN',
  'GUARD', 'GUESS', 'GUEST', 'GUIDE', 'HAPPY',
  'HEART', 'HEAVY', 'HENCE', 'HORSE', 'HOTEL',
  'HOUSE', 'HUMAN', 'IDEAL', 'IMAGE', 'INDEX',
  'INNER', 'INPUT', 'ISSUE', 'JAPAN', 'JOINT',
  'JONES', 'JUDGE', 'JUICE', 'KNIFE', 'KNOCK',
  'KNOWN', 'LABEL', 'LARGE', 'LASER', 'LATER',
  'LAUGH', 'LAYER', 'LEARN', 'LEASE', 'LEAST',
  'LEAVE', 'LEGAL', 'LEVEL', 'LIGHT', 'LIMIT',
  'LOCAL', 'LOOSE', 'LOWER', 'LUCKY', 'LUNCH',
  'MAGIC', 'MAJOR', 'MAKER', 'MARCH', 'MARIA',
  'MATCH', 'MAYBE', 'MAYOR', 'MEANT', 'MEDIA',
  'METAL', 'MIGHT', 'MINOR', 'MINUS', 'MIXED',
  'MODEL', 'MONEY', 'MONTH', 'MORAL', 'MOTOR',
  'MOUNT', 'MOUSE', 'MOUTH', 'MOVIE', 'MUSIC',
  'NEEDS', 'NEVER', 'NEWLY', 'NIGHT', 'NOISE',
  'NORTH', 'NOTED', 'NOVEL', 'NURSE', 'OCCUR',
  'OCEAN', 'OFFER', 'OFTEN', 'ORDER', 'OTHER',
  'OUGHT', 'PAINT', 'PANEL', 'PAPER', 'PARTY',
  'PEACE', 'PETER', 'PHASE', 'PHONE', 'PHOTO',
  'PIECE', 'PILOT', 'PITCH', 'PLACE', 'PLAIN',
  'PLANE', 'PLANT', 'PLATE', 'POINT', 'POUND',
  'POWER', 'PRESS', 'PRICE', 'PRIDE', 'PRIME',
  'PRINT', 'PRIOR', 'PRIZE', 'PROOF', 'PROUD',
  'PROVE', 'QUEEN', 'QUICK', 'QUIET', 'QUITE',
  'RADIO', 'RAISE', 'RANGE', 'RAPID', 'RATIO',
  'REACH', 'REACT', 'READY', 'REALM', 'REFER',
  'REIGN', 'RELAX', 'REPLY', 'RIGHT', 'RIVER',
  'ROBOT', 'ROGER', 'ROMAN', 'ROUGH', 'ROUND',
  'ROUTE', 'ROYAL', 'RURAL', 'SCALE', 'SCENE',
  'SCOPE', 'SCORE', 'SENSE', 'SERVE', 'SEVEN',
  'SHALL', 'SHAPE', 'SHARE', 'SHARP', 'SHEEP',
  'SHEER', 'SHEET', 'SHELF', 'SHELL', 'SHIFT',
  'SHINE', 'SHIRT', 'SHOCK', 'SHOOT', 'SHORT',
  'SHOWN', 'SIGHT', 'SINCE', 'SIXTH', 'SIXTY',
  'SIZED', 'SKILL', 'SLEEP', 'SLIDE', 'SLOPE',
  'SMALL', 'SMART', 'SMILE', 'SMITH', 'SMOKE',
  'SOLID', 'SOLVE', 'SORRY', 'SOUND', 'SOUTH',
  'SPACE', 'SPARE', 'SPEAK', 'SPEED', 'SPEND',
  'SPENT', 'SPLIT', 'SPOKE', 'SPORT', 'STAFF',
  'STAGE', 'STAKE', 'STAND', 'START', 'STATE',
  'STEAM', 'STEEL', 'STEEP', 'STICK', 'STILL',
  'STOCK', 'STONE', 'STOOD', 'STORE', 'STORM',
  'STORY', 'STRIP', 'STUCK', 'STUDY', 'STUFF',
  'STYLE', 'SUGAR', 'SUITE', 'SUPER', 'SWEET',
  'TABLE', 'TAKEN', 'TASTE', 'TAXES', 'TEACH',
  'TEETH', 'TERRY', 'TEXAS', 'THANK', 'THEFT',
  'THEIR', 'THEME', 'THERE', 'THESE', 'THICK',
  'THING', 'THINK', 'THIRD', 'THOSE', 'THREE',
  'THREW', 'THROW', 'TIGHT', 'TIMES', 'TIRED',
  'TITLE', 'TODAY', 'TOKEN', 'TOPIC', 'TOTAL',
  'TOUCH', 'TOUGH', 'TOWER', 'TRACK', 'TRADE',
  'TRAIN', 'TRAIT', 'TRASH', 'TREAT', 'TREND',
  'TRIAL', 'TRIBE', 'TRICK', 'TRIED', 'TROOP',
  'TRUCK', 'TRULY', 'TRUST', 'TRUTH', 'TWICE',
  'UNCLE', 'UNDER', 'UNION', 'UNITY', 'UNTIL',
  'UPPER', 'UPSET', 'URBAN', 'USAGE', 'USUAL',
  'VALID', 'VALUE', 'VIDEO', 'VIRUS', 'VISIT',
  'VITAL', 'VOICE', 'WASTE', 'WATCH', 'WATER',
  'WHEEL', 'WHERE', 'WHICH', 'WHILE', 'WHITE',
  'WHOLE', 'WHOSE', 'WOMAN', 'WORLD', 'WORRY',
  'WORSE', 'WORST', 'WORTH', 'WOULD', 'WOUND',
  'WRITE', 'WRONG', 'WROTE', 'YIELD', 'YOUNG',
  'YOUTH', 'ZEBRA', 'ZONES',
]);

// Convert to array for random selection
const WORD_LIST: string[] = Array.from(DICTIONARY);

export class DictionaryService {
  /**
   * Check if a word is valid (exists in dictionary)
   * Simulates async API call with small delay
   */
  async isValidWord(word: string): Promise<boolean> {
    // Simulate network latency (10-50ms)
    await this.simulateLatency();
    return DICTIONARY.has(word.toUpperCase());
  }

  /**
   * Get a random word from the dictionary
   */
  getRandomWord(): string {
    const index = Math.floor(Math.random() * WORD_LIST.length);
    return WORD_LIST[index];
  }

  /**
   * Get all words (for testing)
   */
  getAllWords(): string[] {
    return [...WORD_LIST];
  }

  private async simulateLatency(): Promise<void> {
    const delay = Math.floor(Math.random() * 40) + 10;
    return new Promise(resolve => setTimeout(resolve, delay));
  }
}
