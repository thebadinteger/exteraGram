package com.exteragram.messenger.export.api;

import com.exteragram.messenger.utils.RecordTag;
import java.util.ArrayList;
import java.util.Objects;

public class ApiWrap$Poll {
    public String question;
    public long id = 0;
    public int totalVotes = 0;
    public boolean closed = false;
    public ArrayList<Answer> answers = new ArrayList<>();

    public static final class Answer extends RecordTag {
        private final boolean my;
        private final byte[] option;
        private final String text;
        private final int votes;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Answer)) {
                return false;
            }
            Answer answer = (Answer) obj;
            return this.my == answer.my && this.votes == answer.votes && Objects.equals(this.text, answer.text) && Objects.equals(this.option, answer.option);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.text, this.option, Integer.valueOf(this.votes), Boolean.valueOf(this.my)};
        }

        public Answer(String str, byte[] bArr, int i, boolean z) {
            this.text = str;
            this.option = bArr;
            this.votes = i;
            this.my = z;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.my, this.votes, this.text, this.option);
        }

        public boolean my() {
            return this.my;
        }

        public String text() {
            return this.text;
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), Answer.class, "text;option;votes;my");
        }

        public int votes() {
            return this.votes;
        }
    }
}
