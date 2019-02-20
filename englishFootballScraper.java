package footballScraper;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class englishFootballScraper {
	public static JSONArray leagueArray=new JSONArray();
	public static String playerName=null;
	public static File leageJSONFile;
	public static String filename ="testfile.txt";
	private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }


	public static void main(String[] args) throws IOException {
		File f1= new File(filename);
		BufferedWriter bw= new BufferedWriter(new FileWriter(f1));
		// TODO Auto-generated method stub
		//String baseurl = "http://www.soccerbase.com/players/player.sd?player_id=";
		int leagueStartYr=2013;
		
		
		for(leagueStartYr=2014;leagueStartYr>=2010;leagueStartYr--){
		
			String baseurl = "http://www.footballsquads.co.uk/eng/";
			String teamNameURL="";
		String teamName="";
		leageJSONFile = new File("engPremLeague"+leagueStartYr+".json");
		baseurl+=leagueStartYr+"-"+(leagueStartYr+1)+"/faprem.htm";
		FileWriter fw=new FileWriter(leageJSONFile);
		
		int cnt=0;
		System.out.println(baseurl);
		Document doc = (Document) Jsoup.connect(baseurl).timeout(10000*10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
        Elements links = doc.select("div > h5");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        
    
        bw.write(links.toString());
        //System.out.println(links.toString());
        
        /*print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            //print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }
*/
        //print("\nLinks: (%d)", links.size());
        for (Element link : links) {
        	
        	//print(link.toString());
        	Elements href = link.select("a[href]");
        	System.out.println(href.attr("href").toString());
        	teamNameURL=href.attr("href").toString();
        	teamName=href.text().toString();
        	fetchPlayerDetails(baseurl, teamNameURL, teamName,leagueStartYr);
        	cnt++;
        	//if(cnt==2) break;
        	       	
        }
        
        /*Elements name = doc.select("div > table > tbody > tr > td > h1");
        print(name.text().toString());
        playerName= name.text().toString();*/
        
		fw.write(leagueArray.toJSONString());
		
		fw.close();
        
        
        System.out.println("FILE written :"+leageJSONFile);
        
		}

		bw.close();
	}

	public static void fetchPlayerDetails(String baseURL, String teamURL, String teamName, int leagueYr) throws IOException{
		String base="http://www.footballsquads.co.uk/eng/"+leagueYr+"-"+(leagueYr+1)+"/";
		String serNo="";
		String playerName="";
		String nat="";
		String Pos="";
		String height="";
		String weight="";
		String dob="";
		String birthPlace="";
		String prevClub="";
		
		
		Document doc = (Document) Jsoup.connect(base+teamURL).timeout(10000*10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
        //System.out.println(base+teamURL);
        
		if(doc.toString().contains("FootballSquads - Page Not Found")) return;
        Elements links = doc.select("div > h5");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        Elements table = doc.select("div > table");
        Elements tableRow=table.select("tbody > tr");
        //System.out.println(tableRow.toString());
        int initcnt=0;
        for(Element row:tableRow){
        	if(initcnt>=1){
        		if(row.toString().contains("Players no longer at this club")){ System.out.println(teamName+" added to "+leageJSONFile); return;}
        	
        	JSONObject obj = new JSONObject();	
        	Elements rowVals= row.select("td");
        	serNo= ((Element) rowVals.toArray()[0]).text().toString();
        	playerName=((Element) rowVals.toArray()[1]).text().toString();
        	nat=((Element) rowVals.toArray()[2]).text().toString();
        	Pos=((Element) rowVals.toArray()[3]).text().toString();
        	height=((Element) rowVals.toArray()[4]).text().toString();
        	weight=((Element) rowVals.toArray()[5]).text().toString();
        	dob=((Element) rowVals.toArray()[6]).text().toString();
        	birthPlace=((Element) rowVals.toArray()[7]).text().toString();
        	prevClub=((Element) rowVals.toArray()[8]).text().toString();
        	//System.out.println(serNo+"||"+playerName+"||"+nat+"||"+Pos+"||"+height+"||"+weight+"||"+dob+"||"+birthPlace+"||"+prevClub+"\n\n");
        	
        	obj.put("TeamName", teamName);
        	obj.put("SerNo", serNo);
        	obj.put("PlayerName", playerName);
        	obj.put("Nationality", nat);
        	obj.put("Position", Pos);
        	obj.put("Height", height);
        	obj.put("DateOfBirth", dob);
        	obj.put("BirthPlace", birthPlace);
        	obj.put("PreviousClub", prevClub);
        	
        	System.out.println(obj.toJSONString());
        	leagueArray.add(obj);
        	
        	}
        	initcnt++;
        	
        	
        }
        
	}
}
