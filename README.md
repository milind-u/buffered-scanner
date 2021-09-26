# buffered-scanner
## Efficient and rich java scanner
- The convenience of `Scanner` combined with the efficiency of `BufferedReader`
- Great for hackathons
### Documentation at https://milind-u.github.io/buffered-scanner/doc/BufferedScanner.html

<br>

## Extreme performance boost
### Stats
- Reading words from `moby_dick.txt`
  - Scanner elapsed time: 0.320939s
  - Buffered Scanner elapsed time: 0.154017s
  - BufferedScanner was 0.166922s better. (47.989607% of Scanner's time)

- Reading doubles from `doubles.txt`
  - Scanner elapsed time: 1.865464s
  - Buffered Scanner elapsed time: 0.259908s
  - BufferedScanner was 1.605556s better. (13.932629% of Scanner's time)

