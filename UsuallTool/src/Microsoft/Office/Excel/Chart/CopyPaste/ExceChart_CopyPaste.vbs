Option Explicit
runVBA

Sub runVBA()
    Dim xl1
    Dim xlBook
    Dim FolderFromPath
    Set xl1 = CreateObject("Excel.application")

    FolderFromPath = Replace(WScript.ScriptFullName, WScript.ScriptName, "")
    set xlBook = xl1.Workbooks.Open(FolderFromPath & "ExcelTemplateCopy4.xlsm")
    xl1.Application.run "'" & xlBook.Name & "'!AlterChiu.copyPaste"
	xl1.DisplayAlerts = False
    xl1.Application.Quit
End Sub