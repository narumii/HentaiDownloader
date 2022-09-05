package cafe.ethyr.hentaidl.data.booru;

public class Site {

    private final String url;
    private final int page;
    private final int amount;

    public Site(String url, int page, int amount) {
        this.url = url;
        this.page = page;
        this.amount = amount;
    }

    public String getUrl() {
        return url;
    }

    public int getPage() {
        return page;
    }

    public int getAmount() {
        return amount;
    }
}
