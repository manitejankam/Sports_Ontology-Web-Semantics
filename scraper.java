package footballScraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class scraper {
	
	public static String playerName=null;
	
	public static String filename ="testfile.txt";
	private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }


	public static void main(String[] args) throws IOException {
		File f1= new File(filename);
		BufferedWriter bw= new BufferedWriter(new FileWriter(f1));
		// TODO Auto-generated method stub
		String baseurl = "http://www.soccerbase.com/players/player.sd?player_id=";
		
		for(int i=2000;i<2200;i++){
		
		Document doc = (Document) Jsoup.connect(baseurl+i).timeout(10000*10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        
    
        bw.write(doc.toString());
        /*print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            //print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }
*/
        //print("\nLinks: (%d)", links.size());
        for (Element link : links) {
        	
        	//print(link.toString());
        	
        	       	
        }
        
        Elements name = doc.select("div > table > tbody > tr > td > h1");
        print(name.text().toString());
        playerName= name.text().toString();
        
		}
        bw.close();


	}

}
