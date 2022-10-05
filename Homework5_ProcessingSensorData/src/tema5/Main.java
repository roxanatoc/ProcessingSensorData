package tema5;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	static List<MonitoredData> lista = new ArrayList<MonitoredData>();
	static SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static PrintWriter out;

	public static void main(String[] args) throws ParseException, IOException {
		out = new PrintWriter("rezultate.txt", "UTF-8");
		task1();
		out.println();

		task2();
		out.println();

		task3();
		out.println();

		task4();
		out.println();

		task5();
		out.println();

		task6();
		out.println();

		out.close();
		System.out.println("GATA!");
	}

	// 1.MONITORED_DATA CLASS
	public static void task1() throws NumberFormatException, IOException {
		out.println("Cerinta 1:");

		try (Stream<String> fisier = Files.lines(Paths.get("activitati.txt"))) {
			lista = fisier.map(s -> s.split("		")).map(s -> new MonitoredData(s[0], s[1], s[2]))
					.collect(Collectors.toList());
			for (int i = 0; i < lista.size(); i++) {
				out.println(lista.get(i).getStartTime() + "	" + lista.get(i).getEndTime() + "	"
						+ lista.get(i).getActivity());
			}
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	// 2.NUMARUL DE ZILE DISTINCTE
	public static void task2() {
		out.println("Cerinta 2:");
		List<String> rezultat = lista.stream().map(x -> x.getStartTime()).collect(Collectors.toList());
		List<String> zile = rezultat.stream().map(s -> s.split(" ")).map(s -> s[0]).collect(Collectors.toList());
		List<String> finale = zile.stream().distinct().collect(Collectors.toList());
		out.println(finale.size() + " zile");
	}

	// 3.NUMARUL TOTAL DE APARITII A FIECAREI ACTIVITATI
	@SuppressWarnings("rawtypes")
	public static void task3() {
		out.println("Cerinta 3:");
		Map<String, Long> rezultat = new TreeMap<String, Long>();
		rezultat = lista.stream().collect(Collectors.groupingBy(x -> x.getActivity(), Collectors.counting()));
		for (Map.Entry activitate : rezultat.entrySet()) {
			out.println(activitate.getKey() + " -> " + activitate.getValue());
		}
	}

	// 4. NUMARUL DE APARITII A FIECARE ACTIVITATI PE ZILE
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void task4() {
		out.println("Cerinta 4:");
		SimpleDateFormat ziua = new SimpleDateFormat("yyyy-MM-dd");
		Map<Object, Map<Object, Long>> rezultat = lista.stream().collect(Collectors.groupingBy(x -> {
			try {
				String s = x.getStartTime();
				String[] data = s.split(" ");
				return ziua.parse(data[0]).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
		}, Collectors.groupingBy(x -> x.getActivity(), Collectors.counting())));
		for (Map.Entry activitate : rezultat.entrySet()) {
			out.println(ziua.format(activitate.getKey()));
			for (Map.Entry activitate1 : ((Map<String, Long>) activitate.getValue()).entrySet()) {
				out.println(activitate1.getKey() + " -> " + activitate1.getValue());
			}
			out.println();
		}
	}

	// 5. TIMPUL TOTAL PENTRU FIECARE ACTIVITATE
	@SuppressWarnings({ "rawtypes" })
	public static void task5() throws ParseException {
		out.println("Cerinta 5:");
		Map<String, Long> rezultat;
		rezultat = lista.stream().collect(Collectors.groupingBy(x -> x.getActivity(), Collectors.summingLong(x -> {
			try {
				return (data.parse(x.getEndTime()).getTime() - data.parse(x.getStartTime()).getTime()) / 1000;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
		})));
		
		for (Map.Entry activitate : rezultat.entrySet()) {
			out.println(activitate.getKey() + " -> " + activitate.getValue() + " sec");
		}
	}

	// 6. ACTIVITATILE CARE AU 90% DIN TIMP MAI MIC DECAT 5 MIN
	public static void task6() {
		out.println("Cerinta 6:");
		Map<String, Long> aparitiiTotale = lista.stream()
				.collect(Collectors.groupingBy(x -> x.getActivity(), Collectors.counting()));
		Map<String, Long> aparitiiLess5 = lista.stream().filter(x -> {
			try {
				return data.parse(x.getEndTime()).getTime() - data.parse(x.getStartTime()).getTime() < 300000;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		}).collect(Collectors.groupingBy(x -> x.getActivity(), Collectors.counting()));
		List<String> rezultat = aparitiiLess5.entrySet().stream()
				.filter(x -> aparitiiTotale.get(x.getKey()) * 0.9 <= x.getValue()).map(x -> x.getKey())
				.collect(Collectors.toList());
		for (String s : rezultat) {
			out.println(s);
		}
	}

}
