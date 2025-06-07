from pocsuite3.api import Output,POCBase,register_poc,requests
class BooleanBindPOC(POCBase):
    vulID='1'
    version='1'
    author='ETsafe'
    vulDate='2024-01-13'
    createDate='2024-01-13'
    updateDate='2024-01-13'
    name='sqli-labs boolean bind injection POC'
    appPowerLink='http://www.sqli-labs.com'
    appName='sqli-labs'
    appVersion='1.1'
    vulType='SQL Injection'
    desc='boolean bind injection'
    samples=['http://127.0.0.1']
    install_requires=[]
    pocDesc='Boolean bind injecion poc by pocsuite3'

    def _verify(self):
        payload=""
        # 
        result={}
        target=self.url
        req=requests.get(target+payload)
        res=req.text
        if('You are in...........' not in res):
            result['VerifyInfo']={}
            result['VerifyInfo']['URL'] = target
            result['VerifyInfo']['payload'] = payload
            return self.parse_output
    def _attack(self):
        return self._verify()
    def parse_output(self,result):
        output=Output(self)
        if result:
            output.success(result)
        else:
            output.fail('target is not vulnerable.')
        return output
register_poc(BooleanBindPOC)
