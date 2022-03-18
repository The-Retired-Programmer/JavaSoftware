{
data:{
        bookings:{match:full, path:"/home/pi/Downloads/club-dinghy-racing-2022-bookings20220316.csv"},
        contacts:{match:startswith, path:"/home/pi/Downloads/enquiries20220222 (2).csv",  resolve:newest}
    },

report1 : {
    title: "Adult Entry Report",
    using: bookings,
    filter:$Event == "Club Dinghy Racing 2022" && $Type == "Entry - Adult",
    fields:[$"Made for",$"Extra info (3)",$"Extra info (4)",$"Extra info (5)",$"Emergency name",$"Emergency number",$"Emergency / medical details"],
    headers:["Made for", $"Extra label (3)", $"Extra label (4)", $"Extra label (5)", "Emergency name", "Emergency number", "Emergency / medical details"]
    },

report2: {
    title: "Check Adult Entries",
    using: bookings,
    fields:[$"Made for",$"Made by", $"Extra info (3)",$"Extra info (4)",$"Extra info (5)",$"Emergency name",$"Emergency number",$"Emergency / medical details"],
    headers:["Made for", "Made by", $"Extra label (3)", $"Extra label (4)", $"Extra label (5)", "Emergency name", "Emergency number", "Emergency / medical details"],
    filter:$Event == "Club Dinghy Racing 2022" && $Type == "Entry - Adult" && 
        ($"Extra info (3)" != "Yes" && $"Extra info (3)" != "No" ||$"Made for" != $"Made by")
    }
}