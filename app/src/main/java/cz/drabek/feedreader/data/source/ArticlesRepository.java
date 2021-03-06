package cz.drabek.feedreader.data.source;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cz.drabek.feedreader.data.Article;
import cz.drabek.feedreader.data.Feed;

import static cz.drabek.feedreader.util.Preconditions.checkNotNull;

public class ArticlesRepository implements ArticlesDataSource {

    public final String TAG = "HONZA-ArticlesRep";

    public static ArticlesRepository INSTANCE = null;
    private final ArticlesDataSource mArticlesRemoteDataSource;
    private final ArticlesDataSource mArticlesLocalDataSource;

    // Prevent direct instantiation.
    private ArticlesRepository(@NonNull ArticlesDataSource articlesRemoteDataSource,
                               @NonNull ArticlesDataSource articlesLocalDataSource) {
        mArticlesRemoteDataSource = checkNotNull(articlesRemoteDataSource);
        mArticlesLocalDataSource = checkNotNull(articlesLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param articlesRemoteDataSource the backend data source
     * @param articlesLocalDataSource  the device storage data source
     * @return the {@link ArticlesRepository} instance
     */
    public static ArticlesRepository getInstance(ArticlesDataSource articlesRemoteDataSource,
                                                 ArticlesDataSource articlesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ArticlesRepository(articlesRemoteDataSource, articlesLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(ArticlesDataSource, ArticlesDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getArticles(@NonNull final LoadArticlesCallback callback) {
        // Get all registered feeds
        mArticlesLocalDataSource.getFeeds(new LoadFeedsCallback() {
            @Override
            public void onFeedsLoaded(List<Feed> feeds) {
                // Load data from server
                mArticlesRemoteDataSource.downloadArticles(feeds, new DownloadArticlesCallback() {
                    @Override
                    public void onArticlesDownloaded(List<Article> articles) {
                        saveToLocalDataStorage(articles);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Log.w(TAG, "onDataNotAvailable: there was an error loading data");
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void saveArticle(@NonNull Article article) { }

    @Override
    public void getArticle(@NonNull final int articleId, @NonNull final GetArticleCallback callback) {
        // Get data from local storage
        mArticlesLocalDataSource.getArticle(articleId, new GetArticleCallback() {
            @Override
            public void onArticleLoaded(Article article) {
                callback.onArticleLoaded(article);
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "onDataNotAvailable: id=" + articleId);
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveFeed(@NonNull Feed feed) {
        mArticlesLocalDataSource.saveFeed(feed);
    }

    @Override
    public void getFeeds(@NonNull LoadFeedsCallback callback) {
        callback.onFeedsLoaded(null);
    }

    private void saveToLocalDataStorage(List<Article> articles) {
        for (Article article: articles)
            mArticlesLocalDataSource.saveArticle(article);
    }

    @Override
    public void getFeed(@NonNull int feedId, @NonNull final GetFeedCallback callback) {
        mArticlesLocalDataSource.getFeed(feedId, new GetFeedCallback() {
            @Override
            public void onFeedLoaded(Feed feed) { callback.onFeedLoaded(feed); }

            @Override
            public void onDataNotAvailable() { }
        });
    }

    @Override
    public void deleteFeed(@NonNull int feedId) {
        mArticlesLocalDataSource.deleteFeed(feedId);
    }

    @Override
    public void downloadArticles(@NonNull List<Feed> feeds, @NonNull DownloadArticlesCallback callback) {
        mArticlesRemoteDataSource.downloadArticles(feeds, new DownloadArticlesCallback() {
            @Override
            public void onArticlesDownloaded(List<Article> articles) {
                saveToLocalDataStorage(articles);
            }

            @Override
            public void onDataNotAvailable() { }
        });
    }
}
