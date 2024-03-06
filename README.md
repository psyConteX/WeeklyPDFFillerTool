Hello to whoever sees this,

the Tool I threw together can be used to automatically create and fill one or more new PDF File(s) based on an existing PDF File with fillable Fields.

it was mostly ment for personal use but if you can get any value out of it feel free to use this

When first used and there is no config.dat, the Tool will create a config.dat File.
In said File is a Beginning Date + an End Date to calculate the Weeks that need to be filled and 4 Filenames: "fileName", "answerName", "varFileName", "randomValueListFileName"
"fileName" needs to be the name of the fillable PDF to proceed, without the File the Tool does not function 
"answerName","varFileName", "randomValueListFileName" do not need to be filled, they can however be filled with a relative File name or an existing Paths followed up by a FileName,
an Example "answerName" :  ".\/example\/variables.json" places the answer Json into the EXISTING directory example into the newly generated File "variables.json"
(on first usage the tool will then try to generate all the default Files, if the PDF is named Input.pdf and is in the Root of the .jar it will proceed with the default values)

it is possible to specify a variable via the varFileName and use that to replace a part of the answerName file, please note the variable does not need to start with % sign
but I advice you to use something unique as if you only take lets say the Character 'n' every 'n' will be replaced with the string following the variable

there are a handfull of special "variables" those are
"%startDate" relates relativ and is exact to the start+7*n
"%endDate"  relates relativ and is exact to the start+7*n+6
"%fridayDate" relates relativ and is exact to the start+7*n+4 
and "%randomValue" is different as it loads a list of strings from the "randomValueListFileName".json and then pops them out of the list into a build string till the list is empty


yeah thats pretty much it
