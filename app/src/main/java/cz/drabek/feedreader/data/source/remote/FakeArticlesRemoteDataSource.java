package cz.drabek.feedreader.data.source.remote;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cz.drabek.feedreader.data.Article;
import cz.drabek.feedreader.data.source.ArticlesDataSource;

public class FakeArticlesRemoteDataSource implements ArticlesDataSource {

    private static FakeArticlesRemoteDataSource INSTANCE = null;

    private List<Article> list;

    // Prevent direct instantiation.
    private FakeArticlesRemoteDataSource() {
        list = new ArrayList<>(20);

        for (int i = 0; i < 20; i++)
            list.add(new Article(
                    i,
                    "Title" + i,
                    "www.datasource" + i + ".com",
                    "Adolf " + i,
                    "Content: " + i));
    }

    public static FakeArticlesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeArticlesRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getArticles(@NonNull LoadArticlesCallback callback) {
        if (list.isEmpty())
            callback.onDataNotAvailable();
        else
            callback.onArticlesLoaded(list);
    }

    @Override
    public void getArticle(@NonNull String articleId, @NonNull GetArticleCallback callback) { }

    @Override
    public void saveArticle(@NonNull Article article) {

    }
}