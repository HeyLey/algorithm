package part2.week14;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class D {

    public static void main(String[] args) throws IOException {
        RdMy in = new RdMy(System.in);

        int n = in.nextInt();

        Random random = new Random();
        Treap t = null;

        for (int i = 0; i < n; i++) {
            if ("+".equals(in.nextWord())) {
                int index = in.nextInt();
                int element = in.nextInt();

                if (t == null) {
                    t = new Treap(element, random.nextInt(), null, null);
                } else {
                    t = t.add(index, element, random);
                }
            } else {
                int left = in.nextInt();
                int right = in.nextInt();
                int result = t.min(left, right);
                System.out.println(result);
            }
        }

    }

    public static class Treap {
        public int element;
        public int y;
        public int size;
        public int min;

        public final Treap left;
        public final Treap right;

        private Treap(int element, int y, Treap left, Treap right) {
            this.element = element;
            this.y = y;
            this.left = left;
            this.right = right;
            recalc();
        }

        public static Treap merge(Treap l, Treap r) {
            if (l == null) return r;
            if (r == null) return l;

            if (l.y > r.y) {
                Treap newR = merge(l.right, r);
                return new Treap(l.element, l.y, l.left, newR);
            } else {
                Treap newL = merge(l, r.left);
                return new Treap(r.element, r.y, newL, r.right);
            }
        }

        public static int sizeOf(Treap treap) {
            return treap == null ? 0 : treap.size;
        }

        public void recalc() {
            size = sizeOf(left) + sizeOf(right) + 1;
            min = element;
            if (left != null) {
                min = Math.min(left.min, min);
            }
            if (right != null) {
                min = Math.min(right.min, min);
            }
        }

        // Разделить на два дерева так что бы в левом было k элементов
        public Treap[] split(int pos) {
            Treap newTree = null;
            Treap l = null;
            Treap r = null;
            int curIndex = sizeOf(left) + 1;

            if (curIndex <= pos) {
                if (right == null) {
                    r = null;
                } else {
                    Treap[] t1 = right.split(pos - curIndex);
                    newTree = t1[0];
                    r = t1[1];
                }
                l = new Treap(this.element, y, left, newTree);
            } else {
                if (left == null) {
                    l = null;
                } else {
                    Treap[] t1 = left.split(pos);
                    l = t1[0];
                    newTree = t1[1];
                }
                r = new Treap(this.element, y, newTree, right);
            }
            return new Treap[]{l, r};
        }


        public Treap add(int pos, int elem, Random random) {
            Treap[] t = split(pos);
            Treap l = t[0];
            Treap r = t[1];
            Treap m = new Treap(elem, random.nextInt(), null, null);
            return merge(merge(l, m), r);
        }

        public void save(int[] result, int offset) {
            if (left != null) {
                left.save(result, offset);
            }
            int curIndex = sizeOf(left);
            result[offset + curIndex] = element;
            if (right != null) {
                right.save(result, offset + curIndex + 1);
            }
        }

        public int min(int l, int r) {
            if (r < 1) {
                return Integer.MAX_VALUE;
            }
            if (l > size) {
                return Integer.MAX_VALUE;
            }
            if (l <= 1 && r >= this.size) {
                return min;
            }
            int min = Integer.MAX_VALUE;
            if (left != null) {
                min = Math.min(min, left.min(l, r));
            }
            int pos = sizeOf(left) + 1;
            if (l <= pos && pos <= r) {
                min = Math.min(min, element);
            }
            if (right != null) {
                min = Math.min(min, right.min(l - pos, r - pos));
            }
            return min;
        }
    }

    static class RdMy {
        BufferedInputStream in;

        final int bufSize = 1 << 16;
        final byte b[] = new byte[bufSize];

        RdMy(InputStream in) {
            this.in = new BufferedInputStream(in, bufSize);
        }

        int nextInt() throws IOException {
            int c;
            while ((c = nextChar()) <= 32)
                ;
            int x = 0, sign = 1;
            if (c == '-') {
                sign = -1;
                c = nextChar();
            }
            while (c >= '0') {
                x = x * 10 + (c - '0');
                c = nextChar();
            }
            return x * sign;
        }

        StringBuilder _buf = new StringBuilder();

        String nextWord() throws IOException {
            int c;
            _buf.setLength(0);
            while ((c = nextChar()) <= 32 && c != -1)
                ;
            if (c == -1)
                return null;
            while (c > 32) {
                _buf.append((char) c);
                c = nextChar();
            }
            return _buf.toString();
        }

        int bn = bufSize, k = bufSize;

        int nextChar() throws IOException {
            if (bn == k) {
                k = in.read(b, 0, bufSize);
                bn = 0;
            }
            return bn >= k ? -1 : b[bn++];
        }

        int nextNotSpace() throws IOException {
            int ch;
            while ((ch = nextChar()) <= 32 && ch != -1)
                ;
            return ch;
        }
    }
}