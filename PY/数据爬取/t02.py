import requests
from bs4 import BeautifulSoup
import pandas as pd

def get_movie_info(url):
    res = requests.get(url)
    soup = BeautifulSoup(res.text, 'html.parser')
    movie_list = soup.find('ol', class_='grid_view').find_all('li')

    data = []
    for movie in movie_list:
        name = movie.find('span', class_='title').text
        rating = movie.find('span', class_='rating_num').text
        data.append([name, rating])
    return data

if __name__ == '__main__':
    url = 'https://movie.douban.com/top250'
    data = get_movie_info(url)
    df = pd.DataFrame(data, columns=['Name', 'Rating'])
    df.to_excel('movies.xlsx', index=False)
