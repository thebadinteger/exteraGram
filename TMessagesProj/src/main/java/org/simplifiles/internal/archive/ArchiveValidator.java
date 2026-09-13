package org.simplifiles.internal.archive;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.archive.ArchiveEntryInfo;
import org.simplifiles.archive.ArchiveInspection;
import org.simplifiles.archive.ArchiveIssue;
import org.simplifiles.archive.ArchiveIssueSeverity;
import org.simplifiles.archive.ValidationReport;
import org.simplifiles.archive.security.DuplicatePolicy;
import org.simplifiles.archive.security.SecurityPolicy;

@Metadata(d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010#\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ,\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\b\u001a\u00020\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002JP\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00150\u00172\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00150\u00172\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002J\u001e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00152\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017H\u0002J\u001e\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u00152\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00150 H\u0002J.\u0010!\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\"2\u0006\u0010\b\u001a\u00020\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002J\u001e\u0010#\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002J&\u0010$\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002J&\u0010%\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002¨\u0006&"}, d2 = {"Lorg/simplifiles/internal/archive/ArchiveValidator;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "validate", "Lorg/simplifiles/archive/ValidationReport;", "inspection", "Lorg/simplifiles/archive/ArchiveInspection;", "policy", "Lorg/simplifiles/archive/security/SecurityPolicy;", "validateEntries", _UrlKt.FRAGMENT_ENCODE_SET, "entries", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveEntryInfo;", "issues", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveIssue;", "validatePathConflict", "entry", "normalizedPath", _UrlKt.FRAGMENT_ENCODE_SET, "filePaths", _UrlKt.FRAGMENT_ENCODE_SET, "fileParentPaths", "directoryPaths", "hasExistingPathAncestor", _UrlKt.FRAGMENT_ENCODE_SET, "path", "existingPaths", "addParentPaths", "parentPaths", _UrlKt.FRAGMENT_ENCODE_SET, "validatePath", "Lorg/simplifiles/internal/archive/ArchivePathAnalysis;", "validateCompressionMethod", "validateEntrySize", "validateCompressionRatio", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ArchiveValidator {
    public static final ArchiveValidator INSTANCE = new ArchiveValidator();

    private ArchiveValidator() {
    }

    public final ValidationReport validate(ArchiveInspection inspection, SecurityPolicy policy) {
        ArrayList arrayList = new ArrayList();
        if (inspection.getEntries().size() > policy.getMaxEntries()) {
            arrayList.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entries.too_many", "Archive contains " + inspection.getEntries().size() + " entries, limit is " + policy.getMaxEntries() + '.', null, 8, null));
        }
        validateEntries(inspection.getEntries(), policy, arrayList);
        return new ValidationReport(inspection.getFormat(), inspection.getEntries(), arrayList);
    }

    private final void validateEntries(List<ArchiveEntryInfo> entries, SecurityPolicy policy, List<ArchiveIssue> issues) {
        ArchiveEntryInfo archiveEntryInfo;
        ArchiveEntryInfo archiveEntryInfo2;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        LinkedHashSet linkedHashSet2 = new LinkedHashSet();
        LinkedHashSet linkedHashSet3 = new LinkedHashSet();
        LinkedHashSet linkedHashSet4 = new LinkedHashSet();
        boolean z = false;
        long uncompressedSize = 0;
        for (ArchiveEntryInfo archiveEntryInfo3 : entries) {
            ArchivePathAnalysis archivePathAnalysisAnalyze = ArchivePathAnalyzer.INSTANCE.analyze(archiveEntryInfo3.getPath());
            validatePath(archiveEntryInfo3, archivePathAnalysisAnalyze, policy, issues);
            validateCompressionMethod(archiveEntryInfo3, issues);
            String normalizedPath = archivePathAnalysisAnalyze.getNormalizedPath();
            if (normalizedPath == null || policy.getDuplicatePolicy() != DuplicatePolicy.ERROR || linkedHashSet.add(normalizedPath)) {
                archiveEntryInfo = archiveEntryInfo3;
            } else {
                archiveEntryInfo = archiveEntryInfo3;
                issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.duplicate", "Archive contains duplicate entry path: " + normalizedPath + '.', archiveEntryInfo.getPath()));
            }
            String normalizedPath2 = archivePathAnalysisAnalyze.getNormalizedPath();
            if (normalizedPath2 != null) {
                ArchiveValidator archiveValidator = INSTANCE;
                archiveEntryInfo2 = archiveEntryInfo;
                archiveValidator.validatePathConflict(archiveEntryInfo2, normalizedPath2, linkedHashSet2, linkedHashSet3, linkedHashSet4, issues);
                if (archiveEntryInfo2.getIsDirectory()) {
                    linkedHashSet2 = linkedHashSet2;
                    linkedHashSet4.add(normalizedPath2);
                } else {
                    linkedHashSet2 = linkedHashSet2;
                    linkedHashSet2.add(normalizedPath2);
                    archiveValidator.addParentPaths(normalizedPath2, linkedHashSet3);
                }
            } else {
                archiveEntryInfo2 = archiveEntryInfo;
                linkedHashSet2 = linkedHashSet2;
            }
            if (!archiveEntryInfo2.getIsDirectory()) {
                validateEntrySize(archiveEntryInfo2, policy, issues);
                validateCompressionRatio(archiveEntryInfo2, policy, issues);
                if (archiveEntryInfo2.getUncompressedSize() >= 0) {
                    if (LongCompanionObject.MAX_VALUE - uncompressedSize >= archiveEntryInfo2.getUncompressedSize()) {
                        uncompressedSize += archiveEntryInfo2.getUncompressedSize();
                    } else if (!z) {
                        issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.total_size.overflow", "Total uncompressed size overflows Long.", null, 8, null));
                        z = true;
                    }
                }
            }
        }
        if (uncompressedSize > policy.getMaxTotalUncompressedSize()) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.total_size.too_large", "Archive uncompressed size is " + uncompressedSize + " bytes, limit is " + policy.getMaxTotalUncompressedSize() + '.', null, 8, null));
        }
    }

    private final void validatePathConflict(ArchiveEntryInfo entry, String normalizedPath, Set<String> filePaths, Set<String> fileParentPaths, Set<String> directoryPaths, List<ArchiveIssue> issues) {
        if (entry.getIsDirectory()) {
            if (!filePaths.contains(normalizedPath) && !hasExistingPathAncestor(normalizedPath, filePaths)) {
                return;
            }
        } else if (!directoryPaths.contains(normalizedPath) && !hasExistingPathAncestor(normalizedPath, filePaths) && !fileParentPaths.contains(normalizedPath)) {
            return;
        }
        issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.path.conflict", "Archive entry path conflicts with another file or directory path: " + normalizedPath + '.', entry.getPath()));
    }

    private final boolean hasExistingPathAncestor(String path, Set<String> existingPaths) {
        int iIndexOf$default = path.indexOf('/');
        while (iIndexOf$default >= 0) {
            if (existingPaths.contains(path.substring(0, iIndexOf$default))) {
                return true;
            }
            iIndexOf$default = path.indexOf('/', iIndexOf$default + 1);
        }
        return false;
    }

    private final void addParentPaths(String path, Set<String> parentPaths) {
        int iIndexOf$default = path.indexOf('/');
        while (iIndexOf$default >= 0) {
            parentPaths.add(path.substring(0, iIndexOf$default));
            iIndexOf$default = path.indexOf('/', iIndexOf$default + 1);
        }
    }

    private final void validatePath(ArchiveEntryInfo entry, ArchivePathAnalysis path, SecurityPolicy policy, List<ArchiveIssue> issues) {
        if (path.getIsEmpty()) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.path.empty", "Archive entry path is empty.", entry.getPath()));
        }
        if (!policy.getAllowAbsolutePaths() && path.getIsAbsolute()) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.path.absolute", "Archive entry path is absolute.", entry.getPath()));
        }
        if (path.getContainsParentTraversal()) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.path.traversal", "Archive entry path contains parent traversal.", entry.getPath()));
        }
    }

    private final void validateCompressionMethod(ArchiveEntryInfo entry, List<ArchiveIssue> issues) {
        if (entry.getCompressionMethod() == 0 || entry.getCompressionMethod() == 8) {
            return;
        }
        issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.method.unsupported", "Archive entry uses unsupported compression method: " + entry.getCompressionMethod() + '.', entry.getPath()));
    }

    private final void validateEntrySize(ArchiveEntryInfo entry, SecurityPolicy policy, List<ArchiveIssue> issues) {
        if (entry.getUncompressedSize() < 0) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.size.unknown", "Archive entry uncompressed size is unknown.", entry.getPath()));
            return;
        }
        if (entry.getUncompressedSize() > policy.getMaxSingleFileSize()) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.size.too_large", "Archive entry is " + entry.getUncompressedSize() + " bytes, limit is " + policy.getMaxSingleFileSize() + '.', entry.getPath()));
        }
    }

    private final void validateCompressionRatio(ArchiveEntryInfo entry, SecurityPolicy policy, List<ArchiveIssue> issues) {
        if (entry.getCompressedSize() < 0 || entry.getUncompressedSize() <= 0) {
            return;
        }
        double uncompressedSize = entry.getCompressedSize() == 0 ? Double.POSITIVE_INFINITY : entry.getUncompressedSize() / entry.getCompressedSize();
        if (uncompressedSize > policy.getMaxCompressionRatio()) {
            issues.add(new ArchiveIssue(ArchiveIssueSeverity.ERROR, "archive.entry.compression_ratio.too_high", "Archive entry compression ratio is " + uncompressedSize + ", limit is " + policy.getMaxCompressionRatio() + '.', entry.getPath()));
        }
    }
}
