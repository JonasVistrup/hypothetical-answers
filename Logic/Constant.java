public class Constant implements Term {


        public String name;
        public Constant(String name){
                this.name = name;
        }

        /**
        *
        * @param substitution
        * @return constant Since a substitution on a constant will not change the constant then this always returns itself.
        */
        @Override
        public Term applySub(Substitution substitution){
                return this;
        }


        @Override
        public boolean equals(Object obj) {
                return super.equals(obj);
        }

        @Override
        public String toString() {
                return name;
        }
}
