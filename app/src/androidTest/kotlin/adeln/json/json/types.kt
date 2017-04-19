package adeln.json.json

data class LfmResults<out T>(
    val results: T
)

data class AlbumSearch(
    val albummatches: AlbumMatches
)

data class AlbumMatches(
    val album: List<LfmAlbum>
)

data class LfmAlbum(
    val mbid: String,
    val artist: String,
    val name: String,
    val image: List<LfmImage>
)

data class LfmImage(
    val size: String
)

data class LfmTag(
    val name: String,
    val url: String
)

data class Tags(
    val tag: List<LfmTag>
)

data class Tracks(
    val track: List<LfmTrack>
)

data class LfmArtist(
    val mbid: String,
    val name: String,
    val url: String
)

data class LfmTrack(
    val artist: LfmArtist,
    val duration: String,
    val name: String,
    val url: String
)

data class LfmAlbumInfo(
    val artist: String,
    val image: List<LfmImage>,
    val tags: Tags,
    val tracks: Tracks,
    val url: String
)
