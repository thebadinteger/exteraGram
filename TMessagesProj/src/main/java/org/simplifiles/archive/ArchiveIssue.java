package org.simplifiles.archive;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class ArchiveIssue {
    private final String code;
    private final String message;
    private final String path;
    private final ArchiveIssueSeverity severity;

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ArchiveIssue)) {
            return false;
        }
        ArchiveIssue archiveIssue = (ArchiveIssue) other;
        return this.severity == archiveIssue.severity && Intrinsics.areEqual(this.code, archiveIssue.code) && Intrinsics.areEqual(this.message, archiveIssue.message) && Intrinsics.areEqual(this.path, archiveIssue.path);
    }

    public int hashCode() {
        int iHashCode = ((((this.severity.hashCode() * 31) + this.code.hashCode()) * 31) + this.message.hashCode()) * 31;
        String str = this.path;
        return iHashCode + (str == null ? 0 : str.hashCode());
    }

    public String toString() {
        return "ArchiveIssue(severity=" + this.severity + ", code=" + this.code + ", message=" + this.message + ", path=" + this.path + ')';
    }

    public ArchiveIssue(ArchiveIssueSeverity archiveIssueSeverity, String str, String str2, String str3) {
        this.severity = archiveIssueSeverity;
        this.code = str;
        this.message = str2;
        this.path = str3;
    }

    public ArchiveIssue(ArchiveIssueSeverity archiveIssueSeverity, String str, String str2, String str3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(archiveIssueSeverity, str, str2, (i & 8) != 0 ? null : str3);
    }

    public final ArchiveIssueSeverity getSeverity() {
        return this.severity;
    }

    public final String getCode() {
        return this.code;
    }

    public final String getMessage() {
        return this.message;
    }

    public final String getPath() {
        return this.path;
    }
}
