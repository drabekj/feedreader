package cz.drabek.feedreader.data.source;


import android.support.annotation.NonNull;

import java.util.List;

import cz.drabek.feedreader.data.Article;

/**
 * Callback interface to inform the user of network/database errors.
 */
public interface ArticlesDataSource {

    interface LoadArticlesCallback {

        void onArticlesLoaded(List<Article> articles);

        void onDataNotAvailable();
    }

    interface GetArticleCallback {

        void onArticleLoaded(Article article);

        void onDataNotAvailable();
    }

    void getArticles(@NonNull LoadArticlesCallback callback);

    void getArticle(@NonNull int articleId, @NonNull GetArticleCallback callback);

    void saveArticle(@NonNull Article article);
}
