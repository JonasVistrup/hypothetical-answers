public class Constant implements Term {
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
}
