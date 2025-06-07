from scrapy.cmdline import execute
import os
import sys

sys.path.append(os.path.dirname(__file__))
execute(["scrapy", "crawl", "movie"])
