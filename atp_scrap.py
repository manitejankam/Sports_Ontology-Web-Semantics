__author__ = 'Hari'
import requests
import bs4
from collections import OrderedDict
import json


root_url = 'http://www.atpworldtour.com'
player_ranking = root_url + '/Rankings/Singles.aspx'
tournament_url = root_url + '/Tournaments/Tournament-Landing.aspx'


def atp_players():
    """ This Method scraps details of top 100 tennis players from ATP tours site.
    """
    response = requests.get(player_ranking)
    soup = bs4.BeautifulSoup(response.text)
    results = [(profile.get_text().encode('utf-8').replace('\xc2\xa0', ' '), profile.attrs.get('href'))
               for profile in soup.select('td.first a[href^=/Tennis]')]
    players = []
    name = dict()

    # Testing code funtionality
    """
    result = results[2]
    
    val = result[0].split(',')
    url = result[1]
    player_dict = {} 
    name['firstname'] = val[1].strip()
    name['lastname'] = val[0].strip()
    player_id = val[1].strip().lower()+val[0].strip().lower()
    player_dict['fullname'] = dict(name)
    player_dict['url'] = url
    player_dict['bio_data'] = get_player_details(url)
    players[player_id] = player_dict 
    players.append(player_dict)
    print players[0]
    """    
    for result in results:
        val = result[0].split(',')
        url = result[1]
        player_records = {}
        name['firstname'] = val[1].strip()
        name['lastname'] = val[0].strip()
        player_id = val[1].strip().lower()+val[0].strip().lower()
        player_records['name'] = name['firstname'] + ' ' + name['lastname']
        player_records['url'] = url
        player_records.update(get_player_details(url))
    	players.append(player_records)
    
    with open('players.json', 'w') as outfile:
        json.dump(players, outfile)
    return json.dumps(players)


def get_player_details(player_url):

    response = requests.get(root_url+player_url)
    soup = bs4.BeautifulSoup(response.text)
    #p = s.select('div#playerBioInfoCardMain')
    bio_info = soup.select('ul#playerBioInfoList > li')
    player_bio = dict()
    info_details = [info.get_text().encode('utf-8') for info in bio_info]
    for detail in info_details:
        data = detail.split(':')
        player_bio[data[0].strip().lower()] = ''.join(data[1:]).strip()

    # Personal History.
    player_history = soup.select('div#personal')
    player_bio['history'] = player_history[0].get_text().encode('utf-8')

    # Player ranking.
    rank = soup.select('div#playerBioInfoRank > span')
    player_bio['ranking'] = rank[0].get_text()

    # Player titles.
    # title_url = [player_title.attrs.get('href').encode('utf-8')
    #              for player_title in soup.select('li#playerBionav_finals a[href^=/Tennis/Players/Top-Players]')]
    # player_title_url = root_url+title_url[0]
    # print player_title_url
    # player_bio['titles'] = get_title_details(player_title_url)

    return player_bio


def get_title_details(player_title_url):

    #response = requests.get(player_title_url)
    #soup = bs4.BeautifulSoup(response.text)
    #titles = soup.select('h5.profileSecondaryTitle')
    #print titles[0].text
    #career_titles = dict()
    #year = soup.select('p.profileCareerStats > span.profileCareerStatsYear')
    #titles = soup.select('p.profileCareerStats > a')
    #print titles[0].get_text()
    #r = re.search(r'\(\*\)', titles[0].text, re.M|re.I)
    #print r.group()
    return player_title_url


def atp_tournaments():
    """ This Method scraps details of all grand slam matches from ATP tours site.
    """
    response = requests.get(tournament_url)
    soup = bs4.BeautifulSoup(response.text)
    result = [(profile.get_text().encode('utf-8').replace('\xc2\xa0', ' '), profile.attrs.get('href'))
              for profile in soup.select('a[href^=/Tennis]')]
    all_tournament_urls = dict(result)
    tournaments = get_all_tournament_stats(all_tournament_urls)
     
    with open('australian_open.json', 'w') as outfile:
	json.dump(tournaments[0], outfile)
  
    with open('wimbledon_open.json', 'w') as outfile:
	json.dump(tournaments[1], outfile)
    
    with open('french_open.json', 'w') as outfile:
	json.dump(tournaments[2], outfile)

    with open('us_open.json', 'w') as outfile:
	json.dump(tournaments[3], outfile)
    
    return json.dumps(tournaments)
    

def get_all_tournament_stats(urls_dict):

    grandslams = ['Australian Open', 'Wimbledon', 'Roland Garros', 'US Open']
    slam_record = list()
    for event in grandslams:
        if event in urls_dict:
            slam_record.append(get_tournament_details(event, root_url+urls_dict[event]))
    return slam_record


def get_tournament_details(slam_name, url):
    """ This Method retrieves details of all the grand slam final matches from ATP tours site.
    """
    #print url
    slam_details = list()
    response = requests.get(url)
    soup = bs4.BeautifulSoup(response.text)
    for index in range(1,16):
        rows = soup.find('table').findAll('tr')[index]
        #print rows
        cols = rows.select('td')
        year = cols[0].get_text()
        score_url = cols[1].select('a')[0].attrs.get('href')
        match_detail = dict()
        match_detail['tournament'] = slam_name
        match_detail['score'] = get_match_details(root_url+score_url)
        match_detail['winner'] = cols[2].get_text().encode('utf-8').replace('\xc2\xa0', ' ')
        match_detail['year'] = year.encode('utf-8')
        slam_details.append(match_detail)
    #print slam_details['2014']['winner']
    #print slam_details.values()
    return slam_details


def get_match_details(url):
    """ This Method gets the score and winner detail for a grand slam final from ATP tours site.
    """
    response = requests.get(url)
    soup = bs4.BeautifulSoup(response.text)
    result = soup.select('a#cphMain_phExtra_ctl00_ctl08_ctl00_ScoreLink')
    return result[0].get_text().encode('utf-8')

if __name__ == '__main__':

    atp_players()
    atp_tournaments()
