package zerobase.stockdividend;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

//@SpringBootApplication
public class StockDividendApplication {

	public static void main(String[] args) {
//		SpringApplication.run(StockDividendApplication.class, args);

		Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1685318400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
		try {
			Document document = connection.get();
			Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");

			Element element = elements.get(0); // table 전체

			Element tbody = element.children().get(1); // tbody

			for (Element e : tbody.children()) {
				String txt = e.text();
				if (!txt.endsWith("Dividend")) {
					continue;
				}

				String[] splits = txt.split(" ");
				String month = splits[0];
				int day = Integer.parseInt(splits[1].replace(",", ""));
				int year = Integer.parseInt(splits[2]);
				String dividend = splits[3];

				System.out.println(year + "/" + month + "/" + day + " -> " + dividend);

			}


		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
