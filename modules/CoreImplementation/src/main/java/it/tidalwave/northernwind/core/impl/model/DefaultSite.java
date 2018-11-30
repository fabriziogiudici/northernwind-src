/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - git clone https://bitbucket.org/tidalwave/northernwind-src.git
 * %%
 * Copyright (C) 2011 - 2018 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id: b6920b63e8220900eb1f9f66a9fbc99337b7b135 $
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.core.impl.model;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.core.model.ResourceFile;
import it.tidalwave.northernwind.core.model.ResourceFileSystem;
import it.tidalwave.northernwind.core.model.Content;
import it.tidalwave.northernwind.core.model.Media;
import it.tidalwave.northernwind.core.model.ModelFactory;
import it.tidalwave.northernwind.core.model.Resource;
import it.tidalwave.northernwind.core.model.ResourcePath;
import it.tidalwave.northernwind.core.model.Site;
import it.tidalwave.northernwind.core.model.SiteFinder;
import it.tidalwave.northernwind.core.model.SiteNode;
import it.tidalwave.northernwind.core.model.spi.LinkPostProcessor;
import it.tidalwave.northernwind.core.model.spi.RequestHolder;
import it.tidalwave.northernwind.core.model.ResourceFileSystemProvider;
import it.tidalwave.northernwind.core.impl.util.RegexTreeMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The default implementation of {@link Site}.
 *
 * @author  Fabrizio Giudici
 * @version $Id: b6920b63e8220900eb1f9f66a9fbc99337b7b135 $
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
/* package */ class DefaultSite implements InternalSite
  {
    @Inject
    private List<LinkPostProcessor> linkPostProcessors;

    @Inject
    private RequestHolder requestHolder;

    @Nonnull
    private final ModelFactory modelFactory;

    @Inject @Named("fileSystemProvider") @Getter
    private ResourceFileSystemProvider fileSystemProvider;

    @Nonnull
    /* package */ final String documentPath;

    @Nonnull
    /* package */ final String mediaPath;

    @Nonnull
    /* package */ final String libraryPath;

    @Nonnull
    /* package */ final String nodePath;

    @Getter
    /* package */ final boolean logConfigurationEnabled;

    @Getter @Nonnull
    /* package */ final String contextPath;

    /* package */ final List<String> ignoredFolders = new ArrayList<>();

    private ResourceFile documentFolder;

    private ResourceFile libraryFolder;

    private ResourceFile mediaFolder;

    @Getter
    private ResourceFile nodeFolder;

    // Note that this class can't be final neither use ImmutableMaps, since during the traversing of the filesystem
    // resources need to access the partially created internal structure.

    /* package */  final Map<String, Content> documentMapByRelativePath = new TreeMap<>();

    /* package */  final Map<String, Resource> libraryMapByRelativePath = new TreeMap<>();

    /* package */  final Map<String, Media> mediaMapByRelativePath = new TreeMap<>();

    /* package */  final Map<String, SiteNode> nodeMapByRelativePath = new TreeMap<>();

    /* package */  final RegexTreeMap<SiteNode> nodeMapByRelativeUri = new RegexTreeMap<>();

    private final Map<Class<?>, Map<String, ?>> relativePathMapsByType = new HashMap<>();

    private final Map<Class<?>, RegexTreeMap<?>> relativeUriMapsByType = new HashMap<>();

    private final List<Locale> configuredLocales = new ArrayList<>();

    private final Predicate<ResourceFile> FOLDER_FILTER =
            file -> file.isFolder() && !ignoredFolders.contains(file.getName());

    private final Predicate<ResourceFile> FILE_FILTER =
            file -> file.isData() && !ignoredFolders.contains(file.getName());

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected DefaultSite (final @Nonnull Site.Builder siteBuilder)
      {
        this.modelFactory = siteBuilder.getModelFactory();
        this.contextPath = siteBuilder.getContextPath();
        this.documentPath = siteBuilder.getDocumentPath();
        this.mediaPath = siteBuilder.getMediaPath();
        this.libraryPath = siteBuilder.getLibraryPath();
        this.nodePath = siteBuilder.getNodePath();
        this.logConfigurationEnabled = siteBuilder.isLogConfigurationEnabled();
        this.configuredLocales.addAll(siteBuilder.getConfiguredLocales());
        this.ignoredFolders.addAll(siteBuilder.getIgnoredFolders());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull @SuppressWarnings("unchecked")
    public <Type> SiteFinder<Type> find (final @Nonnull Class<Type> type)
      {
        final Map<String, Type> relativePathMap = (Map<String, Type>)relativePathMapsByType.get(type);
        final RegexTreeMap<Type> relativeUriMap = (RegexTreeMap<Type>)relativeUriMapsByType.get(type);
        return new DefaultSiteFinder<>(type.getSimpleName(), relativePathMap, relativeUriMap);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String createLink (final @Nonnull ResourcePath relativeUri)
      {
        final ResourcePath link = new ResourcePath(contextPath).appendedWith(relativeUri);
        String linkAsString = requestHolder.get().getBaseUrl() + link.asString();

        for (final LinkPostProcessor linkPostProcessor : linkPostProcessors)
          {
            linkAsString = linkPostProcessor.postProcess(linkAsString);
          }

        return linkAsString;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public List<Locale> getConfiguredLocales()
      {
        return new CopyOnWriteArrayList<>(configuredLocales);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return String.format("DefaultSite(@%x)", System.identityHashCode(this));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    /* package */ void initialize()
      throws IOException, NotFoundException, PropertyVetoException
      {
        log.info("initialize()");

        relativePathMapsByType.put(Content.class, documentMapByRelativePath);
        relativePathMapsByType.put(Media.class, mediaMapByRelativePath);
        relativePathMapsByType.put(Resource.class, libraryMapByRelativePath);
        relativePathMapsByType.put(SiteNode.class, nodeMapByRelativePath);

        relativeUriMapsByType.put(SiteNode.class, nodeMapByRelativeUri);

        log.info(">>>> fileSystemProvider: {}", fileSystemProvider);
        final ResourceFileSystem fileSystem = fileSystemProvider.getFileSystem();
        documentFolder = findMandatoryFolder(fileSystem, documentPath);
        libraryFolder  = findMandatoryFolder(fileSystem, libraryPath);
        mediaFolder    = findMandatoryFolder(fileSystem, mediaPath);
        nodeFolder     = findMandatoryFolder(fileSystem, nodePath);

        log.info(">>>> contextPath:        {}", contextPath);
        log.info(">>>> ignoredFolders:     {}", ignoredFolders);
        log.info(">>>> fileSystem:         {}", fileSystem);
        log.info(">>>> documentPath:       {}", documentFolder.getPath().asString());
        log.info(">>>> libraryPath:        {}", libraryFolder.getPath().asString());
        log.info(">>>> mediaPath:          {}", mediaFolder.getPath().asString());
        log.info(">>>> nodePath:           {}", nodeFolder.getPath().asString());
        log.info(">>>> locales:            {}", configuredLocales);

        documentMapByRelativePath.clear();
        libraryMapByRelativePath.clear();
        mediaMapByRelativePath.clear();
        nodeMapByRelativePath.clear();
        nodeMapByRelativeUri.clear();

        traverse(libraryFolder, FILE_FILTER,
                (file, relativePath) -> libraryMapByRelativePath.put(relativePath.asString(), modelFactory.createResource().withFile(file).build()));

        traverse(mediaFolder, FILE_FILTER,
                (file, relativePath) -> mediaMapByRelativePath.put(relativePath.asString(), modelFactory.createMedia().withFile(file).build()));

        traverse(documentFolder, FOLDER_FILTER,
                (folder, relativePath) -> documentMapByRelativePath.put(relativePath.asString(), modelFactory.createContent().withFolder(folder).build()));

        traverse(nodeFolder, FOLDER_FILTER,
                (folder, relativePath) ->
              {
                try
                  {
                    final SiteNode siteNode = modelFactory.createSiteNode(DefaultSite.this, folder);
                    nodeMapByRelativePath.put(relativePath.asString(), siteNode);

                    if (!siteNode.isPlaceHolder())
                      {
                        final ResourcePath relativeUri = siteNode.getRelativeUri();
                        // Nodes which manage path params are registered with a relativeUri having a wildcard suffix
                        if ("true".equals(siteNode.getProperty(SiteNode.PROPERTY_MANAGES_PATH_PARAMS).orElse("false")))
                          {
                            final String suffix = relativeUri.asString().endsWith("/") ? "(|.*$)" : "(|/.*$)";
                            nodeMapByRelativeUri.putRegex("^" + RegexTreeMap.escape(relativeUri.asString()) + suffix, siteNode);
                          }
                        else
                          {
                            nodeMapByRelativeUri.put(relativeUri.asString(), siteNode);
                          }
                      }
                  }
                catch (IOException | NotFoundException e)
                  {
                    throw new RuntimeException(e);
                  }
              });

        if (logConfigurationEnabled)
          {
            logConfiguration("Documents by relative path:", documentMapByRelativePath);
            logConfiguration("Library by relative path:", libraryMapByRelativePath);
            logConfiguration("Media by relative path:", mediaMapByRelativePath);
            logConfiguration("Nodes by relative path:", nodeMapByRelativePath);
            logConfiguration("Nodes by relative URI:", nodeMapByRelativeUri);
          }
      }

    /*******************************************************************************************************************
     *
     * Traverse the file system with a {@link Predicate}.
     *
     * @param  folder      the folder to traverse
     * @param  fileFilter  the filter for directory contents
     * @param  predicate   the predicate
     *
     ******************************************************************************************************************/
    private void traverse (final @Nonnull ResourceFile folder,
                           final @Nonnull Predicate<ResourceFile> fileFilter,
                           final @Nonnull BiConsumer<ResourceFile, ResourcePath> predicate)
      {
        traverse(folder.getPath(), folder, fileFilter, predicate);
      }

    /*******************************************************************************************************************
     *
     * Traverse the file system with a {@link Predicate}.
     *
     * @param  file        the file to traverse
     * @param  fileFilter  the filter for directory contents
     * @param  predicate   the predicate
     *
     ******************************************************************************************************************/
    private void traverse (final @Nonnull ResourcePath rootPath,
                           final @Nonnull ResourceFile file,
                           final @Nonnull Predicate<ResourceFile> fileFilter,
                           final @Nonnull BiConsumer<ResourceFile, ResourcePath> predicate)
      {
        log.trace("traverse({}, {}, {}, {})", rootPath, file, fileFilter, predicate);
        final ResourcePath relativePath = file.getPath().urlDecoded().relativeTo(rootPath);

        if (fileFilter.test(file))
          {
            predicate.accept(file, relativePath);
          }

        file.findChildren().results().forEach(child -> traverse(rootPath, child, fileFilter, predicate));
      }

    /*******************************************************************************************************************
     *
     * Logs the configuration contained in the given map of properties.
     *
     ******************************************************************************************************************/
    private static void logConfiguration (final @Nonnull String name, Map<String, ?> propertyMap)
      {
        log.info(name);

        for (final Entry<String, ?> entry : propertyMap.entrySet())
          {
            log.info(">>>> {}: {}", entry.getKey(), entry.getValue());
          }
      }

    /*******************************************************************************************************************
     *
     * FIXME Wrapper against ResourceFileSystem: its methods should throw NFE by themselves
     *
     ******************************************************************************************************************/
    @Nonnull
    private static ResourceFile findMandatoryFolder (final @Nonnull ResourceFileSystem fileSystem,
                                                     final @Nonnull String path)
      throws NotFoundException
      {
        return NotFoundException.throwWhenNull(fileSystem.findFileByPath(path), "Cannot find folder: " + path);
        // don't log fileSystem.getRoot() since if fileSystem is broken it can trigger secondary errors
                            // FileUtil.toFile(fileSystem.getRoot()).getAbsolutePath() + "/"  + path);
      }
  }
