#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
GitHub自动关注脚本 - Playwright版本
使用playwright连接本地Chrome实例(端口9222)自动关注GitHub仓库
"""

import asyncio
import logging
import re
from playwright.async_api import async_playwright, Browser, Page
from typing import List, Optional, Tuple

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class GitHubFollowerPlaywright:
    def __init__(self, debug_port: int = 9222):
        """
        初始化GitHub关注器
        :param debug_port: Chrome调试端口，默认9222
        """
        self.debug_port = debug_port
        self.browser: Optional[Browser] = None
        self.page: Optional[Page] = None
        
        # GitHub仓库链接列表
        self.github_links = [
    "https://github.com/yhy0",
    "https://github.com/GhostTroops",
    "https://github.com/Soufaker",
    "https://github.com/bit4woo",
    "https://github.com/ffffffff0x",
    "https://github.com/projectdiscovery",
    "https://github.com/chushuai",
    "https://github.com/0x7eTeam",
    "https://github.com/DeEpinGh0st",
    "https://github.com/qiwentaidi",
    "https://github.com/yqcs",
    "https://github.com/wafinfo",
    "https://github.com/cseroad",
    "https://github.com/rustdesk",
    "https://github.com/chaitin",
    "https://github.com/spyboy-productions",
    "https://github.com/co01cat",
    "https://github.com/FindAllTeam",
    "https://github.com/honmashironeko",
    "https://github.com/Potato-py",
    "https://github.com/kracer127",
    "https://github.com/niudaii",
    "https://github.com/zhengjim",
    "https://github.com/SleepingBag945",
    "https://github.com/z2p",
    "https://github.com/EdgeSecurityTeam",
    "https://github.com/youki992",
    "https://github.com/d78ui98",
    "https://github.com/webraybtl",
    "https://github.com/ExpLangcn",
    "https://github.com/Hypdncy",
    "https://github.com/tongchengbin",
    "https://github.com/swisskyrepo",
    "https://github.com/shadow1ng",
    "https://github.com/Li4n0",
    "https://github.com/smallfox233",
    "https://github.com/fankun99",
    "https://github.com/wudijun",
    "https://github.com/F6JO",
    "https://github.com/sulab999",
    "https://github.com/RustScan",
    "https://github.com/z-bool",
    "https://github.com/sleeyax",
    "https://github.com/shuanx",
    "https://github.com/hmx222",
    "https://github.com/kkbo8005",
    "https://github.com/M0nster3",
    "https://github.com/R4gd0ll",
    "https://github.com/en0th",
    "https://github.com/Janhsu",
    "https://github.com/T4y1oR",
    "https://github.com/s1g0day",
    "https://github.com/pureqh",
    "https://github.com/yuag",
    "https://github.com/taomujian",
    "https://github.com/Ashro-one",
    "https://github.com/zhensuibianwan",
    "https://github.com/mifine666",
    "https://github.com/suizhibo",
    "https://github.com/Sec-Fork",
    "https://github.com/Pizz33",
    "https://github.com/AgentVirus",
    "https://github.com/xzajyjs",
    "https://github.com/rtcatc",
    "https://github.com/xiaokp7",
    "https://github.com/jdr2021",
    "https://github.com/Abs1n7he",
    "https://github.com/enomothem",
    "https://github.com/testnet0",
    "https://github.com/smxiazi",
    "https://github.com/fdx-xdf",
    "https://github.com/r0oth3x49",
    "https://github.com/xingyunsec",
    "https://github.com/kN6jq",
    "https://github.com/MInggongK",
    "https://github.com/xiaoxiaoranxxx",
    "https://github.com/EASY233",
    "https://github.com/P001water",
    "https://github.com/x364e3ab6",
    "https://github.com/qiuluo-oss",
    "https://github.com/AabyssZG",
    "https://github.com/testzboy",
    "https://github.com/Ackites",
    "https://github.com/Ghr07h"
    "https://github.com/watanabe-hsad",
    "https://github.com/Snow-Mountain-Passengers",
    "https://github.com/eeeeeeeeee-code",
    "https://github.com/Chave0v0",
    "https://github.com/xiaogang000",
    "https://github.com/doki-byte",
    "https://github.com/Scorcsoft",
    "https://github.com/T3nk0",
    "https://github.com/banchengkemeng",
    "https://github.com/P001water",
    "https://github.com/1ucky7",
    "https://github.com/JaveleyQAQ",
    "https://github.com/intbjw",
    "https://github.com/Conan924",
    "https://github.com/outlaws-bai",
    "https://github.com/saoshao"
]
    
    async def setup_browser(self) -> bool:
        """
        设置浏览器，连接到本地Chrome实例
        """
        try:
            playwright = await async_playwright().start()
            
            # 连接到本地Chrome实例
            self.browser = await playwright.chromium.connect_over_cdp(
                f"http://localhost:{self.debug_port}"
            )
            
            # 获取现有的页面或创建新页面
            contexts = self.browser.contexts
            if contexts:
                context = contexts[0]
                pages = context.pages
                if pages:
                    self.page = pages[0]
                else:
                    self.page = await context.new_page()
            else:
                context = await self.browser.new_context()
                self.page = await context.new_page()
            
            # 设置用户代理和其他选项
            await self.page.set_extra_http_headers({
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
            })
            
            logger.info(f"成功连接到Chrome实例 (端口: {self.debug_port})")
            return True
            
        except Exception as e:
            logger.error(f"连接Chrome失败: {e}")
            logger.info("请确保Chrome已启动并开启调试模式:")
            logger.info("chrome.exe --remote-debugging-port=9222 --user-data-dir=\"C:\\temp\\chrome_debug\"")
            return False
    
    def extract_username_from_url(self, repo_url: str) -> str:
        """
        从GitHub仓库URL中提取用户名
        """
        try:
            # 从 https://github.com/username/repo 中提取 username
            parts = repo_url.strip('/').split('/')
            if len(parts) >= 4 and parts[2] == 'github.com':
                return parts[3]
        except Exception:
            pass
        return ""
    
    async def find_follow_button(self, username: str):
        """
        查找Follow/Unfollow用户按钮
        """
        if not username:
            return None, False
            
        # 首先尝试查找Unfollow按钮（已关注状态）
        try:
            unfollow_button = self.page.get_by_role('button', name=f'Unfollow {username}')
            if await unfollow_button.is_visible(timeout=1000):
                return unfollow_button, True  # 返回按钮和已关注状态
        except Exception:
            pass
        
        # 查找Follow按钮（未关注状态）
        try:
            follow_button = self.page.get_by_role('button', name=f'Follow {username}')
            if await follow_button.is_visible(timeout=1000):
                return follow_button, False  # 返回按钮和未关注状态
        except Exception:
            pass
        
        # 备用选择器 - 使用正则表达式匹配
        try:
            unfollow_button = self.page.get_by_role('button', name=re.compile(f'Unfollow {re.escape(username)}'))
            if await unfollow_button.is_visible(timeout=1000):
                return unfollow_button, True
        except Exception:
            pass
            
        try:
            follow_button = self.page.get_by_role('button', name=re.compile(f'Follow {re.escape(username)}'))
            if await follow_button.is_visible(timeout=1000):
                return follow_button, False
        except Exception:
            pass
        
        # 更通用的选择器
        selectors = [
            f'button:has-text("Follow {username}")',
            f'button:has-text("Unfollow {username}")',
            'button:has-text("Follow")',
            'button:has-text("Unfollow")',
            '[data-testid="follow-button"]'
        ]
        
        for selector in selectors:
            try:
                element = self.page.locator(selector).first
                if await element.is_visible(timeout=1000):
                    # 检查按钮文本判断是否已关注
                    button_text = await element.text_content()
                    is_following = button_text and 'unfollow' in button_text.lower()
                    return element, is_following
            except Exception:
                continue
        
        return None, False
    

    
    async def click_follow_button(self, button) -> bool:
        """
        点击关注用户按钮
        """
        try:
            # 滚动到按钮位置
            await button.scroll_into_view_if_needed()
            await asyncio.sleep(0.5)
            
            # 点击按钮
            await button.click()
            await asyncio.sleep(1)
            
            return True
        except Exception as e:
            logger.error(f"点击Follow按钮失败: {e}")
            return False
    

    
    async def process_repository(self, repo_url: str, index: int, total: int) -> bool:
        """
        处理单个仓库，关注仓库创建者
        """
        # 提取用户名
        username = self.extract_username_from_url(repo_url)
        if not username:
            logger.error(f"  ✗ 无法从URL提取用户名: {repo_url}")
            return False
            
        logger.info(f"正在处理第 {index + 1}/{total} 个仓库: {repo_url} (用户: {username})")
        
        try:
            # 访问仓库页面，增加超时时间和更宽松的等待策略
            try:
                await self.page.goto(repo_url, wait_until='domcontentloaded', timeout=60000)
                # 等待页面基本加载完成
                await asyncio.sleep(3)
            except Exception as e:
                logger.warning(f"  页面加载超时，尝试继续: {e}")
                # 如果超时，尝试等待更长时间让页面加载
                await asyncio.sleep(5)
            
            # 查找Follow按钮
            follow_button, is_following = await self.find_follow_button(username)
            
            if follow_button:
                if is_following:
                    logger.info(f"  ✓ 用户 {username} 已关注，跳过")
                    return True
                else:
                    if await self.click_follow_button(follow_button):
                        logger.info(f"  ✓ 成功关注用户: {username}")
                        return True
                    else:
                        logger.warning(f"  ⚠ 关注用户 {username} 失败")
                        return False
            else:
                logger.warning(f"  ⚠ 未找到用户 {username} 的Follow按钮")
                return False
                
        except Exception as e:
            logger.error(f"  ✗ 处理仓库时出错: {e}")
            return False
    
    async def run(self) -> bool:
        """
        运行自动关注脚本
        """
        if not await self.setup_browser():
            return False
        
        try:
            total_repos = len(self.github_links)
            success_count = 0
            
            logger.info(f"开始处理 {total_repos} 个GitHub仓库，关注仓库创建者")
            
            for i, repo_url in enumerate(self.github_links):
                if await self.process_repository(repo_url, i, total_repos):
                    success_count += 1
                
                # 延迟避免请求过于频繁
                await asyncio.sleep(1.5)
            
            logger.info(f"处理完成！成功关注用户: {success_count}/{total_repos}")
            return True
            
        except KeyboardInterrupt:
            logger.info("用户中断操作")
            return False
        except Exception as e:
            logger.error(f"运行过程中出错: {e}")
            return False
        finally:
            if self.browser:
                await self.browser.close()
                logger.info("浏览器已关闭")

async def main():
    """
    主函数
    """
    print("GitHub自动关注用户脚本 - Playwright版本")
    print("=" * 50)
    print("功能: 自动关注GitHub仓库的创建者")
    print("使用前请确保:")
    print("1. 已安装Chrome浏览器")
    print("2. 已安装playwright: pip install playwright")
    print("3. 已安装浏览器: playwright install chromium")
    print("4. Chrome已启动调试模式:")
    print('   chrome.exe --remote-debugging-port=9222 --user-data-dir="C:\\temp\\chrome_debug"')
    print("5. 已在Chrome中登录GitHub账号")
    print("=" * 50)
    
    input("按回车键开始执行...")
    
    follower = GitHubFollowerPlaywright(debug_port=9222)
    await follower.run()

if __name__ == "__main__":
    asyncio.run(main())