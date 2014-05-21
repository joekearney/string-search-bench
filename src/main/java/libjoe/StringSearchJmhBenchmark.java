package libjoe;

import static libjoe.strings.util.SampleStrings.HUNDRED_A_B;
import static libjoe.strings.util.SampleStrings.PARAGRAPH;
import static libjoe.strings.util.SampleStrings.SENTENCE;
import static libjoe.strings.util.SampleStrings.SINGLE_CHAR;
import static libjoe.strings.util.SampleStrings.THOUSAND_A;
import static libjoe.strings.util.SampleStrings.TWELFTH_NIGHT;
import static libjoe.strings.util.SampleStrings.WORD;
import libjoe.strings.StringMatch;
import libjoe.strings.StringMatcher;
import libjoe.strings.StringSearchAlgorithms;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.base.Optional;

@State(Scope.Benchmark)
public class StringSearchJmhBenchmark {
	enum BenchmarkCases {
		ONE_CHAR_in_SENTENCE(SINGLE_CHAR, SENTENCE),
		WORD_in_SENTENCE(WORD, SENTENCE),
		SENTENCE_in_LONGER_SENTENCE(SENTENCE, WORD + SENTENCE + WORD),
		SENTENCE_in_SHAKESPEARE(SENTENCE, TWELFTH_NIGHT),
		LONG_MISSING_in_LONGER(HUNDRED_A_B, THOUSAND_A),
		PARAGRAPH_in_SHAKESPEARE(PARAGRAPH, TWELFTH_NIGHT),
		;

		private final String needle;
		private final String haystack;

		private BenchmarkCases(String needle, String haystack) {
			this.haystack = haystack;
			this.needle = needle;
		}
	}

	@Param(value = { "ONE_CHAR_in_SENTENCE", "WORD_in_SENTENCE", "SENTENCE_in_LONGER_SENTENCE", "SENTENCE_in_SHAKESPEARE", "LONG_MISSING_in_LONGER",
			"PARAGRAPH_in_SHAKESPEARE" })
	public String benchmarkCase;
	@Param(value = { "STRING_INDEX_OF", "BRUTE_FORCE", "RABIN_KARP", "KNUTH_MORRIS_PRATT", "AHO_CORASICK_LIB" })
	public String algorithm;

	private StringMatcher matcher;
	private CharSequence haystack;
	private String needle;

	@Setup
	public void setup() {
		BenchmarkCases benchmarkCaseResolved = BenchmarkCases.valueOf(benchmarkCase);
		needle = benchmarkCaseResolved.needle;
		matcher = StringSearchAlgorithms.valueOf(algorithm).get().matchPattern(needle);
		haystack = benchmarkCaseResolved.haystack;
	}

	@GenerateMicroBenchmark
	public Optional<StringMatch> testMatch() {
		return matcher.search(haystack);
	}

	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(".*" + StringSearchJmhBenchmark.class.getSimpleName() + ".*")
		 .warmupIterations(2)
		 .measurementIterations(2)
		 .forks(1)
		 .mode(Mode.SampleTime).build();
		new Runner(options).run();
	}
}
