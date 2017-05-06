package cz.drabek.feedreader.data.source;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cz.drabek.feedreader.data.Article;
import cz.drabek.feedreader.data.source.remote.FakeArticlesRemoteDataSource;
import static cz.drabek.feedreader.util.Preconditions.checkNotNull;

public class ArticlesRepository implements ArticlesDataSource {

    public final String TAG = "ArticlesRepository";

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

    // TODO currently implemented for mock static data
    @Override
    public void getArticles(@NonNull final LoadArticlesCallback callback) {
        final FakeArticlesRemoteDataSource mMockData = new FakeArticlesRemoteDataSource();

        mMockData.getArticles(new LoadArticlesCallback() {
            @Override
            public void onArticlesLoaded(List<Article> articles) {
                callback.onArticlesLoaded(articles);
            }

            @Override
            public void onDataNotAvailable() {
                Log.w(TAG, "onDataNotAvailable: there was an error loading data");
            }
        });
    }

    @Override
    public void saveArticle(@NonNull Article article) {

    }

    @Override
    public void getArticle(@NonNull String articleId, @NonNull GetArticleCallback callback) { }
}
