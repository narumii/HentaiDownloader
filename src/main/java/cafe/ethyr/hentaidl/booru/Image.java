package cafe.ethyr.hentaidl.booru;

public class Image {

    private final String image;
    private final String fileUrl;

    public Image(String image, String fileUrl) {
        this.image = image;
        this.fileUrl = fileUrl;
    }

    public String getImage() {
        return image;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
