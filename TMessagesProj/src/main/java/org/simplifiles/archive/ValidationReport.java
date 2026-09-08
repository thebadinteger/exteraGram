package org.simplifiles.archive;

import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\f\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004¢\u0006\u0004\b\t\u0010\nJ\u0010\u0010\f\u001a\u00020\u000bHÖ\u0001¢\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u000eHÖ\u0001¢\u0006\u0004\b\u000f\u0010\u0010J\u001a\u0010\u0013\u001a\u00020\u00122\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0013\u0010\u0014R\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0015\u001a\u0004\b\u0016\u0010\u0017R\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0018\u001a\u0004\b\u0019\u0010\u001aR\u001d\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00048\u0006¢\u0006\f\n\u0004\b\b\u0010\u0018\u001a\u0004\b\u001b\u0010\u001aR\u0011\u0010\u001c\u001a\u00020\u00128F¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001d¨\u0006\u001e"}, d2 = {"Lorg/simplifiles/archive/ValidationReport;", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveFormat;", "format", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveEntryInfo;", "entries", "Lorg/simplifiles/archive/ArchiveIssue;", "issues", "<init>", "(Lorg/simplifiles/archive/ArchiveFormat;Ljava/util/List;Ljava/util/List;)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Lorg/simplifiles/archive/ArchiveFormat;", "getFormat", "()Lorg/simplifiles/archive/ArchiveFormat;", "Ljava/util/List;", "getEntries", "()Ljava/util/List;", "getIssues", "isSafe", "()Z", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nValidationReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ValidationReport.kt\norg/simplifiles/archive/ValidationReport\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,17:1\n2792#2,3:18\n*S KotlinDebug\n*F\n+ 1 ValidationReport.kt\norg/simplifiles/archive/ValidationReport\n*L\n12#1:18,3\n*E\n"})
public final /* data */ class ValidationReport {
    private final List<ArchiveEntryInfo> entries;
    private final ArchiveFormat format;
    private final List<ArchiveIssue> issues;

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ValidationReport)) {
            return false;
        }
        ValidationReport validationReport = (ValidationReport) other;
        return this.format == validationReport.format && Intrinsics.areEqual(this.entries, validationReport.entries) && Intrinsics.areEqual(this.issues, validationReport.issues);
    }

    public int hashCode() {
        ArchiveFormat archiveFormat = this.format;
        return ((((archiveFormat == null ? 0 : archiveFormat.hashCode()) * 31) + this.entries.hashCode()) * 31) + this.issues.hashCode();
    }

    public String toString() {
        return "ValidationReport(format=" + this.format + ", entries=" + this.entries + ", issues=" + this.issues + ')';
    }

    public ValidationReport(ArchiveFormat archiveFormat, List<ArchiveEntryInfo> list, List<ArchiveIssue> list2) {
        this.format = archiveFormat;
        this.entries = list;
        this.issues = list2;
    }

    public final ArchiveFormat getFormat() {
        return this.format;
    }

    public /* synthetic */ ValidationReport(ArchiveFormat archiveFormat, List list, List list2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : archiveFormat, (i & 2) != 0 ? CollectionsKt.emptyList() : list, (i & 4) != 0 ? CollectionsKt.emptyList() : list2);
    }

    public final List<ArchiveEntryInfo> getEntries() {
        return this.entries;
    }

    public final List<ArchiveIssue> getIssues() {
        return this.issues;
    }

    public final boolean isSafe() {
        List<ArchiveIssue> list = this.issues;
        if ((list instanceof Collection) && list.isEmpty()) {
            return true;
        }
        for (ArchiveIssue archiveIssue : list) {
            if (archiveIssue.getSeverity() == ArchiveIssueSeverity.ERROR || archiveIssue.getSeverity() == ArchiveIssueSeverity.BLOCKER) {
                return false;
            }
        }
        return true;
    }
}
