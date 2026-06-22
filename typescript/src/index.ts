import { WordleService } from './WordleService';
import { LetterCode } from './types';

/**
 * Demo script that exercises the WordleService so you can observe its behaviour.
 *
 * Run with: npm run demo
 */

function codeToEmoji(code: LetterCode): string {
  switch (code) {
    case LetterCode.GREEN: return '🟩';
    case LetterCode.YELLOW: return '🟨';
    case LetterCode.GREY: return '⬜';
  }
}

function formatResult(guess: string, codes: LetterCode[]): string {
  const letters = guess.split('').map((l, i) => `${l}:${codeToEmoji(codes[i])}`).join(' ');
  return letters;
}

async function demonstrateDuplicateLetters() {
  console.log('='.repeat(60));
  console.log('Test Case: Duplicate Letter Handling');
  console.log('='.repeat(60));
  
  const service = new WordleService();
  
  // Test case: Answer is PAPER, guess is APPLE
  const gameId = service.startGame({ answer: 'PAPER' });
  const result = await service.submitGuess(gameId, 'APPLE');
  
  console.log('\nAnswer: PAPER');
  console.log('Guess:  APPLE');
  console.log('\nResult:');
  console.log(formatResult(result.guess, result.codes));

  // Another test case
  console.log('\n' + '-'.repeat(40));
  
  const gameId2 = service.startGame({ answer: 'SHEEP' });
  const result2 = await service.submitGuess(gameId2, 'CREEP');
  
  console.log('\nAnswer: SHEEP');
  console.log('Guess:  CREEP');
  console.log('\nResult:');
  console.log(formatResult(result2.guess, result2.codes));
}

async function demonstrateValidation() {
  console.log('\n' + '='.repeat(60));
  console.log('Test Case: Input Validation');
  console.log('='.repeat(60));

  const service = new WordleService();
  const gameId = service.startGame({ answer: 'REACT' });

  console.log('\nAnswer: REACT (5 letters)');

  console.log('\nSubmitting "HI" (2 letters)...');
  try {
    const result = await service.submitGuess(gameId, 'HI');
    console.log('⚠️  Accepted:', formatResult(result.guess, result.codes));
  } catch (e) {
    console.log('✅ Rejected:', (e as Error).message);
  }

  console.log('\nSubmitting "XXXXX" (not in dictionary)...');
  try {
    const result = await service.submitGuess(gameId, 'XXXXX');
    console.log('⚠️  Accepted:', formatResult(result.guess, result.codes));
  } catch (e) {
    console.log('✅ Rejected:', (e as Error).message);
  }
}

async function demonstrateConcurrency() {
  console.log('\n' + '='.repeat(60));
  console.log('Test Case: Concurrent Requests');
  console.log('='.repeat(60));

  const service = new WordleService();
  const gameId = service.startGame({ answer: 'REACT', maxGuesses: 2 });

  console.log('\nAnswer: REACT');
  console.log('Max guesses: 2');
  console.log('\nSubmitting 3 guesses simultaneously...');

  const promises = [
    service.submitGuess(gameId, 'APPLE'),
    service.submitGuess(gameId, 'BRAVE'),
    service.submitGuess(gameId, 'CRANE'),
  ];

  const results = await Promise.allSettled(promises);
  const successful = results.filter(r => r.status === 'fulfilled');
  const failed = results.filter(r => r.status === 'rejected');

  console.log(`\n✓ Successful: ${successful.length}`);
  console.log(`✗ Rejected: ${failed.length}`);

  if (successful.length > 0) {
    successful.forEach((r, i) => {
      if (r.status === 'fulfilled') {
        console.log(`  ${i + 1}. ${r.value.guess} - Remaining: ${r.value.remainingGuesses}`);
      }
    });
  }

  const game = service.getGame(gameId);
  console.log(`\nFinal game state: ${game?.guesses.length} guesses recorded`);
  console.log(`Expected: Maximum 2 guesses`);
}

async function main() {
  console.log('🎮 Wordle Service Test Suite\n');
  console.log('Run `npm test` to see which tests are failing.\n');

  await demonstrateDuplicateLetters();
  await demonstrateValidation();
  await demonstrateConcurrency();

  console.log('\n' + '='.repeat(60));
  console.log('Phase 1: Fix the failing tests');
  console.log('Phase 2: Choose a track and build production features');
  console.log('='.repeat(60));
}

main().catch(console.error);
