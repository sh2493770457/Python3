import scrapy

from douban.douban.items import DoubanItem


class MovieSpider(scrapy.Spider):
    name = "movie"
    allowed_domains = ["movie.douban.com"]
    start_urls = ["https://movie.douban.com/top250"]

    def parse(self, response):
        movie_list= response.xpath("//div[@class='article']//ol[@class='grid_view']/li")[0:3]
        for i_item in movie_list:
            douban_item=DoubanItem()
            douban_item['serial_number']=i_item.xpath(".//div[@class='item']//em/text()").extract_first("")
            count=i_item.xpath(".//div[@class='bd']/p[1]/text()").extract()
            for i_count in count:
                count_s="".join(i_count.split())
                douban_item['movie_introduce']=count_s
            douban_item['star']=i_item.xpath(".//div[@class='star']/span[2]/text()").extract_first("")
            douban_item['evaluate']=i_item.xpath(".//div[@class='star']//span[4]/text()").extract_first("")
            douban_item['describe']=i_item.xpath(".//div[@class='bd']//span[@class='inq']/text()").extract_first("")
            print(douban_item)
            yield douban_item
