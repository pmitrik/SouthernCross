This code reads in two files, a JSON and CSV formatted file.  It pulls out the latitude and longitude
along with an UUID and date/time.  It compares the two files for any exact matches and should only use one. It
then looks for the oldest and newest entries.  The results are posted to a JSON formatted file that contain
the number of entries and unique entries along with the oldest and newest entries.