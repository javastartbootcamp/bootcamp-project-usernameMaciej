package pl.javastart.bootcamp.domain.github;


import com.jcabi.github.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class GithubService {

    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);

    public static final String TOKEN = "insertokenhere";
    public static final String BOT_USERNAME = "insertusernamehere";

    public String cloneRepository(String baseRepository, String githubUsername, String repoName, String[] trainers) {

        logger.debug("Preparing repo {} for user {} with name {}" , baseRepository, githubUsername, repoName);

        try {
            Repo repo = findOrCreateRepo(repoName);

            File gitBaseDir = new File("gitrepos");

            File repoLocationOnDisc = new File(gitBaseDir, BOT_USERNAME + "/" + repoName);

            CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(TOKEN, "");

            FileUtils.deleteDirectory(gitBaseDir);

            repoLocationOnDisc.mkdirs();

            String uri = baseRepository;
            if (baseRepository.startsWith("https://github")) {
                String repoEnding = getRepositoryEnding(baseRepository);
                uri = "https://" + TOKEN + "@github.com/" + repoEnding;
            }

            logger.debug("Cloning {}" , uri);

            Git git = Git.cloneRepository()
                    .setURI(uri)
                    .setDirectory(repoLocationOnDisc)
                    .setCredentialsProvider(credentialsProvider)
                    .call();

            String remoteUrl = "https://" + TOKEN + "@github.com/" + BOT_USERNAME + "/" + repoName + ".git";

            logger.debug("Pushing to {}" , remoteUrl);

            git.push()
                    .setRemote(remoteUrl)
                    .setForce(true)
                    .setCredentialsProvider(credentialsProvider)
                    .call();

            git.close();

            try {
                FileUtils.deleteDirectory(gitBaseDir);
            } catch (Exception e) {
                e.printStackTrace();
            }

            repo.collaborators().add(githubUsername);

            for (String trainer : trainers) {
                logger.debug("Adding trainer {}" , trainer);
                repo.collaborators().add(trainer.trim());
            }

            logger.debug("Repo created successfully trainer");

            return "https://github.com/" + BOT_USERNAME + "/" + repoName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Repo findOrCreateRepo(String repoName) throws IOException {
        Github github = new RtGithub(TOKEN);
        Repo repo;
        Coordinates.Simple coords = new Coordinates.Simple(BOT_USERNAME, repoName);
        boolean alreadyExists = github.repos().exists(coords);
        if (!alreadyExists) {
            Repos.RepoCreate settings = new Repos.RepoCreate(repoName, true);
            repo = github.repos().create(settings);
        } else {
            repo = github.repos().get(coords);
        }
        return repo;
    }

    private String getRepositoryEnding(String baseRepository) {
        String[] split = baseRepository.split("/");
        return split[split.length - 2] + "/" + split[split.length - 1];
    }

}
